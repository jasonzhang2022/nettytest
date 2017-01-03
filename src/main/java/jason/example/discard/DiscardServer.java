package jason.example.discard;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;

public class DiscardServer {

	
	
	public static void main(String[] args) throws InterruptedException{
		
		EventLoopGroup boss = new NioEventLoopGroup(1);
		EventLoopGroup worker = new NioEventLoopGroup(3);
		
		try  {
			ServerBootstrap server= new ServerBootstrap();
			server.group(boss, worker);
			server.channel(NioServerSocketChannel.class);
			server.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							try {
								ByteBuf buf = ByteBuf.class.cast(msg);
								System.out.println(buf.toString(Charset.forName("UTF-8")));
							} finally {
								ReferenceCountUtil.release(msg);
							}
						}
					});
				}
			});
			server.option(ChannelOption.SO_BACKLOG, 5);
			server.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			//setup is ready. we are ready to connect
			
			ChannelFuture channelFuture=server.bind(8080).addListener(new ChannelFutureListener(){
				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("Server: bind ti 8080 correctly");
				}
			});
			
			//wait for channel to close
			channelFuture.channel().closeFuture().sync();
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
		
	}
}
