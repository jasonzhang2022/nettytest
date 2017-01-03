package jason.example.discard1;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class DiscardClient {

	DiscardClientHandler writer = new DiscardClientHandler();
	EventLoopGroup eventLoop;

	public void init() throws InterruptedException {
		eventLoop = new NioEventLoopGroup();
			Bootstrap bootStrap = new Bootstrap();
			bootStrap.group(eventLoop).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addFirst(new LoggingHandler(LogLevel.INFO));
							ch.pipeline().addFirst(writer);

						}
					});
			ChannelFuture channelFuture = bootStrap.connect("localhost", 8080).sync();
			channelFuture.addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("client: future complete from init");
					
				}
				
			});
	
	}

	public static void main(String[] args) throws InterruptedException {

		final DiscardClient client = new DiscardClient();
		client.init();
		client.writer.write("hello world2").addListener(new ChannelFutureListener(){

			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.printf("client: send message in main thread. success=%s\n", future.isSuccess()?"true":"false");
				
				
				
				future.channel().close();
				client.eventLoop.shutdownGracefully();
			}
			
		});
		
		client.eventLoop.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		
		
		System.out.println("we come here");
	}

}
