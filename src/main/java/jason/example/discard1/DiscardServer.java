package jason.example.discard1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class DiscardServer {
	
	int port;

	public DiscardServer(int port) {
		super();
		this.port = port;
	}
	
	public void run() throws InterruptedException{
		NioEventLoopGroup serverGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap server=new ServerBootstrap();
			//server setup.
			server
				.group(serverGroup, workerGroup)//how many thread to handle the event.
				.channel(NioServerSocketChannel.class)// where data comes from
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>(){

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new DiscardServerHandler());
					}
					
				}) //how the channel should be handled, initialize pipeline
				.option(ChannelOption.SO_BACKLOG, 128) //allow 128 client at backlg
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future=server.bind(port).sync();
			
			future.channel().closeFuture().sync();
		} finally{
			serverGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		new DiscardServer(8080).run();
	}
}
