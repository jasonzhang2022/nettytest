package jason.example.time;

import java.nio.charset.Charset;
import java.util.Date;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TimeClient {
	public static void main(String[] args) throws InterruptedException{
	
		EventLoopGroup clientThread = new NioEventLoopGroup();
		
		try {
			Bootstrap client = new Bootstrap();
			client.group(clientThread);
			client.channel(NioSocketChannel.class);
			client.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
						ByteBuf buf = null;
						
						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							System.out.println("byte becomes active");
							buf = ctx.alloc().buffer(8);
						}
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							ByteBuf buf1 = ByteBuf.class.cast(msg);
							
							buf.writeBytes(buf1);
							buf1.release();
							if (buf.readableBytes()>=8){
								Date d = new Date(buf.readLong());
								System.out.println("date is "+d.toString());
								buf.release();
								ctx.close();
							}							
						}

						@Override
						public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
							System.out.println("read complete");
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							cause.printStackTrace();
							ctx.close();
						}
						
					});
				}
				
			});
			
			client.option(ChannelOption.TCP_NODELAY, true);
			client.connect("localhost", 8081).sync().channel().closeFuture().sync();
			
			
		} finally {
			clientThread.shutdownGracefully();
		}
		
		
	}
	

}
