package jason.example.protobuf;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

	
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
					//add length field
					ch.pipeline().addLast(new LengthFieldPrepender(2));
					
					//convert user to byte array
					ch.pipeline().addLast(new UserEncoder());
					
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							System.out.println("server: channel is active");
							//write a user.
							User user = new User();
							user.age=10;
							user.firstName= "first name";
							user.lastName =" zhang";
							user.birthDate= new Date();
							ctx.pipeline().writeAndFlush(user);
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println("server: data is avaialbale for read");
							
						
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
