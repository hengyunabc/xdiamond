package io.github.xdiamond.net;

import io.github.xdiamond.domain.Profile;
import io.github.xdiamond.domain.Project;
import io.github.xdiamond.domain.vo.ResolvedConfig;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.xdiamond.common.net.Commands;
import io.xdiamond.common.net.Message;
import io.xdiamond.common.net.Request;
import io.xdiamond.common.net.Response;
import io.xdiamond.common.net.Response.ResponseBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class XDiamondServerHandler extends SimpleChannelInboundHandler<Message> {
  static final private Logger logger = LoggerFactory.getLogger(XDiamondServerHandler.class);

  ProjectService projectService;
  ProfileService profileService;
  ConfigService configService;

  static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  static AttributeKey<ConnectionInfo> connectionInfoKey = AttributeKey.valueOf("connectionInfo");

  public XDiamondServerHandler(ProjectService projectService, ProfileService profileService,
      ConfigService configService) {
    this.projectService = projectService;
    this.profileService = profileService;
    this.configService = configService;
  }

  /**
   * 得到所有的连接的客户端的信息
   */
  public static List<ConnectionInfo> getConnectionsInfo() {
    List<ConnectionInfo> infoList = new LinkedList<>();
    Iterator<Channel> iterator = channels.iterator();
    while (iterator.hasNext()) {
      Channel channel = iterator.next();
      Attribute<ConnectionInfo> connectionInfoValue = channel.attr(connectionInfoKey);
      ConnectionInfo connectionInfo = connectionInfoValue.get();
      infoList.add(connectionInfo);
    }
    return infoList;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    channels.add(ctx.channel());
    super.channelActive(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
    if (msg.getType() == Message.REQUEST) {
      Message responseMsg = new Message();
      responseMsg.setType(Message.RESPONSE);

      Request request = JSON.parseObject(msg.getData(), Request.class);
      // response里的id设置为request的id
      ResponseBuilder responseBuilder = Response.builder().id(request.getId());

      if (request.getCommand() == Commands.GET_CONFIG) {
        String groupId = (String) request.dataValue("groupId");
        String artifactId = (String) request.dataValue("artifactId");
        String version = (String) request.dataValue("version");
        String profile = (String) request.dataValue("profile");
        String secretKey = (String) request.dataValue("secretKey");
        Project project = projectService.select(groupId, artifactId, version);
        if (project == null) {
          Response response =
              responseBuilder.fail().withErrorMessage("project do not exist!")
                  .command(request.getCommand()).build();
          responseMsg.setData(JSON.toJSONBytes(response));
          ctx.writeAndFlush(responseMsg);
          return;
        }
        Profile projectProfile = profileService.selectByProjectIdAndName(project.getId(), profile);
        if (projectProfile == null) {
          Response response =
              responseBuilder.fail().withErrorMessage("profile do not exist! " + profile)
                  .command(request.getCommand()).build();
          responseMsg.setData(JSON.toJSONBytes(response));
          ctx.writeAndFlush(responseMsg);
          return;
        }

        // 如果profile的SecretKey是空/null ，则也可以获取到
        if (StringUtils.isNotEmpty(projectProfile.getSecretKey())
            && !StringUtils.equals(projectProfile.getSecretKey(), secretKey)) {
          Response response =
              responseBuilder.fail().withErrorMessage("secretKey is wrong!")
                  .command(request.getCommand()).build();
          responseMsg.setData(JSON.toJSONBytes(response));
          ctx.writeAndFlush(responseMsg);
          return;
        }

        List<ResolvedConfig> resolvedConfigList =
            configService.listResolvedConfig(projectProfile.getId());
        Response response =
            responseBuilder.success().withResult("configs", resolvedConfigList)
                .command(request.getCommand()).build();
        responseMsg.setData(JSON.toJSONBytes(response));
        ctx.writeAndFlush(responseMsg);

        // 把客户端连接信息写到Attr里
        ConnectionInfo connectionInfo =
            new ConnectionInfo(groupId, artifactId, version, profile, ctx.channel().remoteAddress()
                .toString());
        ctx.channel().attr(connectionInfoKey).set(connectionInfo);
        return;
      }
    }

  }
}
