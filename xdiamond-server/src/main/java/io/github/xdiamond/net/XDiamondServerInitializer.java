package io.github.xdiamond.net;

import io.github.xdiamond.common.net.MessageDecoder;
import io.github.xdiamond.common.net.MessageEncoder;
import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class XDiamondServerInitializer extends ChannelInitializer<SocketChannel> {

  ProjectService projectService;
  ProfileService profileService;
  ConfigService configService;

  int readTimeout = 15;
  int writeTimeout = 5;

  public XDiamondServerInitializer(ProjectService projectService, ProfileService profileService,
      ConfigService configService) {
    this.projectService = projectService;
    this.profileService = profileService;
    this.configService = configService;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
    pipeline.addLast(new IdleStateHandler(readTimeout, writeTimeout,
        readTimeout > writeTimeout ? readTimeout : writeTimeout));
    pipeline.addLast(new MessageDecoder());
    pipeline.addLast(new MessageEncoder());
    pipeline.addLast(new XDiamondServerHandler(projectService, profileService, configService));
  }

}
