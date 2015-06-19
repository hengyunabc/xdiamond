package io.github.xdiamond.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.xdiamond.common.ConfigVO;
import io.xdiamond.common.ResolvedConfigVO;
import io.xdiamond.common.net.Message;
import io.xdiamond.common.net.MessageDecoder;
import io.xdiamond.common.net.MessageEncoder;

import java.util.List;

public class XDiamondClient {

	String serverAddress;
	int port = 5678;
	int readTimeout = 5;
	int writeTimeout = 5;

	int reconnectDelaySeconds = 3;

	public XDiamondClient(String serverAddress, int port) {
		super();
		this.serverAddress = serverAddress;
		this.port = port;
	}

	// TODO 改为一个线程？
	EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

	ClientHandler clientHandler = new ClientHandler(this);

	public ChannelFuture init() {
		return configureBootstrap(new Bootstrap(), eventLoopGroup).connect();
	}

	public void destory() {
		eventLoopGroup.shutdownGracefully();
	}

	public Future<List<ResolvedConfigVO>> getConfigs(String groupId, String artifactId,
			String version, String profile, String secretKey) {
		return clientHandler.getConfig(groupId, artifactId, version, profile, secretKey);
	}

	Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
		b.group(g).channel(NioSocketChannel.class)
				.remoteAddress(serverAddress, port)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						clientHandler = new ClientHandler(XDiamondClient.this);
						ch.pipeline()
								.addLast(
										new LoggingHandler(LogLevel.DEBUG),
										// new IdleStateHandler(readTimeout,
										// writeTimeout,
										// readTimeout > writeTimeout ?
										// readTimeout :
										// writeTimeout),
										new MessageEncoder(), 
										new MessageDecoder(), clientHandler);
					}
				});

		return b;
	}

	public int getReconnectDelaySeconds() {
		return reconnectDelaySeconds;
	}

	public void setReconnectDelaySeconds(int reconnectDelaySeconds) {
		this.reconnectDelaySeconds = reconnectDelaySeconds;
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
}
