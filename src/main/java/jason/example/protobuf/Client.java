package jason.example.protobuf;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Client {
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
					//use length field, and strip the length field.
					ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2));
					//convert byte to user.
					ch.pipeline().addLast(new UserDecoder());
					//dump user
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
						
						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							System.out.println("byte becomes active");							
						}
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							User user = User.class.cast(msg);
							
							System.out.printf("byte is avaialle for reading %s\n", user.toString());
							ctx.close();
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
