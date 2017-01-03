package jason.example.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

public class DiscardClient {

	public static void main(String[] args) throws InterruptedException {

		EventLoopGroup eventThread = new NioEventLoopGroup();
		try {
			Bootstrap client = new Bootstrap();
			client.group(eventThread);
			client.channel(NioSocketChannel.class);
			client.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
					
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							//send two messages and quit.
							ctx.writeAndFlush(Unpooled.wrappedBuffer("hello world".getBytes())).addListener( new ChannelFutureListener(){

								public void operationComplete(ChannelFuture future) throws Exception {
									System.out.printf("finished first write: success=%s\n", future.isSuccess()?"true":"false");
									
									
									future.channel().writeAndFlush(Unpooled.wrappedBuffer("second string ".getBytes()).retain()).addListener(new ChannelFutureListener(){

										public void operationComplete(ChannelFuture future1) throws Exception {
											
											System.out.printf("finished second write: success=%s\n", future1.isSuccess()?"true":"false");
											
											future1.channel().close().addListener(new ChannelFutureListener(){

												public void operationComplete(ChannelFuture future) throws Exception {
													System.out.println("channel is closed");
												}
												
											});
											
										}
										
									});
									
								}
								
							});
							
						}

						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							System.out.println("byte is avaialle for reading");
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
			
			
			//set up is ready, we are ready to connect
			ChannelFuture channelFuture = client.connect("localhost", 8080).addListener(new ChannelFutureListener(){

				public void operationComplete(ChannelFuture future) throws Exception {
					System.out.println("connect is established");
				}
			});
			//wait for channel close.
			channelFuture.channel().closeFuture().sync();
			
		} finally {
			eventThread.shutdownGracefully();
		}

	}
}
