package io.github.xdiamond.client.net;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.common.ResolvedConfigVO;
import io.github.xdiamond.common.net.MessageDecoder;
import io.github.xdiamond.common.net.MessageEncoder;
import io.github.xdiamond.common.util.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XDiamondClient {
  static final Logger logger = LoggerFactory.getLogger(XDiamondClient.class);

  String serverAddress;
  int port = 5678;
  int readTimeout = 15;
  int writeTimeout = 5;

  boolean daemon = true;

  // 指数退避的方式增加
  boolean bBackOffRetryInterval = true;
  // 失败重试的次数
  int maxRetryTimes = Integer.MAX_VALUE;
  // 失败重试的时间间隔
  int retryIntervalSeconds = 5;
  // 默认最大的重试时间间隔，2分钟
  int maxRetryIntervalSeconds = 2 * 60;

  // 当前已经重试的次数
  int currentRetryTimes = 0;

  XDiamondConfig xDiamondConfig;

  public XDiamondClient() {
  }

  public XDiamondClient(XDiamondConfig xDiamondConfig, String serverAddress, int port,
      boolean bBackOffRetryInterval, int maxRetryTimes, int retryIntervalSeconds,
      int maxRetryIntervalSeconds, boolean daemon) {
    super();
    this.xDiamondConfig = xDiamondConfig;
    this.serverAddress = serverAddress;
    this.port = port;
    this.bBackOffRetryInterval = bBackOffRetryInterval;
    this.maxRetryTimes = maxRetryTimes;
    this.retryIntervalSeconds = retryIntervalSeconds;
    this.maxRetryIntervalSeconds = maxRetryIntervalSeconds;
    this.daemon = daemon;
  }

  // TODO 改为一个线程？
  EventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat(
          "xdiamond-clientNioEventLoop-thread-%d").setDaemon(daemon).build());
  Bootstrap bootstrap = new Bootstrap();

  ClientHandler clientHandler = new ClientHandler(this);

  public ChannelFuture init() {
    bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000)
        .option(ChannelOption.TCP_NODELAY, true);

    return configureBootstrap(bootstrap, eventLoopGroup).connect();
  }

  public void destory() {
    eventLoopGroup.shutdownGracefully();
  }

  public Future<List<ResolvedConfigVO>> getConfigs(String groupId, String artifactId,
      String version, String profile, String secretKey) {
    return clientHandler.getConfig(groupId, artifactId, version, profile, secretKey);
  }

  Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
    b.group(g).channel(NioSocketChannel.class).remoteAddress(serverAddress, port)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            clientHandler = new ClientHandler(XDiamondClient.this);
            if (logger.isDebugEnabled()) {
              ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
            ch.pipeline().addLast(
                new IdleStateHandler(readTimeout, writeTimeout,
                    readTimeout > writeTimeout ? readTimeout : writeTimeout), new MessageEncoder(),
                new MessageDecoder(), clientHandler);
          }
        });

    return b;
  }

  /**
   * only call by ClientHandler
   */
  void channelActive(ChannelHandlerContext ctx) {
    // 当client连接到server时，重连次数重置
    currentRetryTimes = 0;
  }

  /**
   * only call by ClientHandler
   */
  void channelUnregistered(final ChannelHandlerContext ctx) {
    currentRetryTimes++;
    if (currentRetryTimes > maxRetryTimes) {
      return;
    }

    int currentRetryInterval = retryIntervalSeconds;
    if (bBackOffRetryInterval) {
      // 注意currentRetryInterval 可能会溢出
      currentRetryInterval = retryIntervalSeconds * (currentRetryTimes >= 30 ? 1 << 30 : 1 << currentRetryTimes);
      if (currentRetryInterval <= 0) {
        currentRetryInterval = maxRetryIntervalSeconds;
      }
    }
    if (currentRetryInterval > maxRetryIntervalSeconds) {
      currentRetryInterval = maxRetryIntervalSeconds;
    }
    logger.info("Waiting for " + currentRetryInterval + "s to reconnect");

    final EventLoop loop = ctx.channel().eventLoop();
    loop.schedule(new Runnable() {
      @Override
      public void run() {
        logger.info("Reconnecting to {}:{}", serverAddress, port);
        configureBootstrap(new Bootstrap(), loop).connect().addListener(new FutureListener<Void>() {
          @Override
          public void operationComplete(Future<Void> future) throws Exception {
            if (!future.isSuccess()) {
              logger.error("can not connection to {}:{}", serverAddress, port, future.cause());
            } else {
              logger.info("connection to {}:{} success.", serverAddress, port);
            }
          }
        });
      }
    }, currentRetryInterval, TimeUnit.SECONDS);
  }

  /**
   * only call by ClientHandler，服务器通知Client配置有更新
   *
   * @return
   */
  void notifyConfigChanged() {
    xDiamondConfig.notifyConfigChanged();
  }

  public String getServerAddress() {
    return serverAddress;
  }

  public void setServerAddress(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
}
