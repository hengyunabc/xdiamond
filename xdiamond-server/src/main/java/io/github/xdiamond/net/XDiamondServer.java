package io.github.xdiamond.net;

import io.github.xdiamond.service.ConfigService;
import io.github.xdiamond.service.ProfileService;
import io.github.xdiamond.service.ProjectService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class XDiamondServer {

  @Value("${xdiamond.server.host:0.0.0.0}")
  private String host = "0.0.0.0";
  @Value("${xdiamond.server.port:5678}")
  private int port = 5678;

  EventLoopGroup bossGroup = new NioEventLoopGroup();
  EventLoopGroup workerGroup = new NioEventLoopGroup();

  @Autowired
  ProjectService projectService;

  @Autowired
  ProfileService profileService;
  @Autowired
  ConfigService configService;

  @PostConstruct
  public void init() throws InterruptedException {
    ServerBootstrap b = new ServerBootstrap();
    b.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);

    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .childHandler(new XDiamondServerInitializer(projectService, profileService, configService));

    b.bind(host, port).sync().channel().closeFuture().addListener(new FutureListener<Object>() {
      @Override
      public void operationComplete(Future<Object> arg0) throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
      }
    });
  }

  @PreDestroy
  public void destory() {
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }

}
