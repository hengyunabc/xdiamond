package io.github.xdiamond.client.net;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.github.xdiamond.common.ResolvedConfigVO;
import io.github.xdiamond.common.net.Commands;
import io.github.xdiamond.common.net.Message;
import io.github.xdiamond.common.net.Oneway;
import io.github.xdiamond.common.net.Request;
import io.github.xdiamond.common.net.Response;
import io.github.xdiamond.common.util.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

/**
 * ClientHandler is not a @Sharable handler, so can't be added or removed multiple times.
 *
 * @author hengyunabc
 *
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {

  static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  static Timer timer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat(
          "xdiamond-clientHandler-timer-thread-%d").setDaemon(true).build());

  static DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(1, new ThreadFactoryBuilder().setNameFormat(
	          "xdiamond-ExecutorGroup-thread-%d").setDaemon(true).build());

  AtomicInteger msgIdGenerator = new AtomicInteger(1);

  XDiamondClient xDiamondClient;

  Channel channel = null;

  // 超时的id通过Listener，Timer清理掉
  Map<Integer, Promise<?>> idPromiseMap = new ConcurrentHashMap<Integer, Promise<?>>();

  public ClientHandler(XDiamondClient xDiamondClient) {
    this.xDiamondClient = xDiamondClient;
  }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    this.channel = ctx.channel();
    super.channelRegistered(ctx);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xDiamondClient.channelActive(ctx);
    super.channelActive(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
    if (msg.getType() == Message.RESPONSE) {
      Response response = msg.dataToResponse();

      // 统一处理Response是失败的情况
      if (!response.isSuccess()) {
        Promise<?> promise = idPromiseMap.get(response.getId());
        promise.setFailure(new RuntimeException(response.errorMessage()));
        return;
      }

      if (response.getCommand() == Commands.GET_CONFIG) {
        JSONArray configs = (JSONArray) response.resultValue("configs");
        List<ResolvedConfigVO> ResolvedConfigVOList = new ArrayList<>(configs.size());
        for (Object object : configs) {
          ResolvedConfigVO ResolvedConfigVO =
              JSON.toJavaObject((JSONObject) object, ResolvedConfigVO.class);
          ResolvedConfigVOList.add(ResolvedConfigVO);
        }
        @SuppressWarnings("unchecked")
        Promise<List<ResolvedConfigVO>> promise =
            (Promise<List<ResolvedConfigVO>>) idPromiseMap.get(response.getId());
        if (promise != null && !promise.isDone()) {
          promise.setSuccess(ResolvedConfigVOList);
        }
      }
    }

    if (msg.getType() == Message.ONEWAY) {
      Oneway oneway = msg.dataToOneway();
      if (oneway.getCommand() == Commands.CONFIG_CHANGED) {
        xDiamondClient.notifyConfigChanged();
      }
    }
  }

  public Future<List<ResolvedConfigVO>> getConfig(String groupId, String artifactId,
      String version, String profile, String secretKey) {
    // 如果没有连接上，或者可能网络中断等，直接返回FailedFuture。
    if (channel == null || !channel.isActive()) {
      return new FailedFuture<List<ResolvedConfigVO>>(eventExecutorGroup.next(),
          new ConnectException("channel is not available"));
    }

    final Request request =
        Request.builder().command(Commands.GET_CONFIG)
            .withData("groupId", groupId).withData("artifactId", artifactId)
            .withData("version", version).withData("profile", profile)
            .withData("secretKey", secretKey).build();

    @SuppressWarnings("unchecked")
    Promise<List<ResolvedConfigVO>> promise =
        (Promise<List<ResolvedConfigVO>>) this.createRequestPromise(channel, request);

    channel.writeAndFlush(Message.request().jsonData(request).build());
    // 这里返回promise，让调用者等待？
    return promise;
  }

  /**
   * 给request设置id，并返回一个与这个request关联的Promise，promise会自动清理
   *
   * @param channel
   * @param request
   * @return
   */
  private Promise<?> createRequestPromise(Channel channel, Request request) {
    Promise<?> promise = new DefaultPromise<Object>(channel.eventLoop());
    final int requestId = msgIdGenerator.getAndIncrement();
    request.setId(requestId);
    idPromiseMap.put(requestId, promise);
    // 用Listener，无论Promise成功或者失败，都会清理掉已完成的Promise
    promise.addListener(new FutureListener<Object>() {
      @Override
      public void operationComplete(Future<Object> arg0) throws Exception {
        idPromiseMap.remove(requestId);
      }
    });
    // 在Timer里清理掉过期的promise
    timer.newTimeout(new TimerTask() {
      @Override
      public void run(Timeout arg0) throws Exception {
        Promise<?> removedPromise = idPromiseMap.remove(requestId);
        if (removedPromise != null && removedPromise.isDone() == false)
          removedPromise.setFailure(new TimeoutException("no response from server, timeout!"));
      }
    }, 30, TimeUnit.SECONDS);
    return promise;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (!(evt instanceof IdleStateEvent)) {
      ctx.fireUserEventTriggered(evt);
      return;
    }

    // 当空闲时，发心跳包
    IdleStateEvent e = (IdleStateEvent) evt;
    if (e.state() == IdleState.WRITER_IDLE) {
      final Request request =
          Request.builder().command(Commands.HEARTBEAT).build();
      ctx.write(Message.request().jsonData(request).build());
    } else if (e.state() == IdleState.READER_IDLE) {
      // 长时间没有收到服务器的回应，说明网络出现问题
      logger.error("long time do not reveive data from server, please check network.");
      ctx.channel().close();
    }
  }

  @Override
  public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
    xDiamondClient.channelUnregistered(ctx);
    this.channel = null;
  }
}
