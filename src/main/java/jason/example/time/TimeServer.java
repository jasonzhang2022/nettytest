package jason.example.time;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TimeServer {

	
	public static void main(String[] args) throws InterruptedException{
		
		
		
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(boss, worker);
			server.channel(NioServerSocketChannel.class);
			server.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							long time = new Date().getTime();
							ByteBuf buf= Unpooled.buffer(4);
							buf.writeLong(time);
							ctx.writeAndFlush(buf).addListener(new ChannelFutureListener(){
								public void operationComplete(ChannelFuture future) throws Exception {
									System.out.println("close client channel after write time");
									future.channel().close();
								}
								
							});
							
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println("server: data is avaialbale for read");
							//write received message back to client.
							ctx.writeAndFlush(msg);
						}

						@Override
						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							System.out.println("server: data is read");
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}
						
					});
					
				}
				
			});
			
			server.bind("localhost", 8081).sync().channel().closeFuture().sync();
			
			
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
		
		
		
	}
	
	
}
