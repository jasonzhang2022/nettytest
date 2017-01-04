package jason.example.discardcorssthread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import io.netty.util.concurrent.CompleteFuture;

public class DiscardClient {
	
	
	
	public static CompletableFuture<ChannelHandlerContext> write(CompletableFuture<ChannelHandlerContext> future, String msg){
		return future.thenCompose( ctx -> {
			
			CompletableFuture<ChannelHandlerContext> ret = new CompletableFuture<>();
			
			ctx.writeAndFlush(Unpooled.wrappedBuffer(msg.getBytes()).retain()).addListener(cf->{
				if (cf.isSuccess()){
					ret.complete(ctx);
				} else {
					ret.completeExceptionally(cf.cause());
				}
			
			});
			
			return ret;
		});
	}

	public static void main(String[] args) throws InterruptedException {
		final CompletableFuture<ChannelHandlerContext> future = new CompletableFuture<>();

		EventLoopGroup eventThread = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		client.group(eventThread);
		client.channel(NioSocketChannel.class);
		client.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));

				ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelActive(ChannelHandlerContext ctx1) throws Exception {
						future.complete(ctx1);
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

		// set up is ready, we are ready to connect
		client.connect("localhost", 8080).addListener(new ChannelFutureListener() {

			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("connect is established");
			}
		});

		final CompletableFuture<ChannelHandlerContext>  hello1 = write(future, "hello world");
		final CompletableFuture<ChannelHandlerContext> hello2 = write(future, "hello world again");
		final CompletableFuture<ChannelHandlerContext> hello3 = write(future, "hello world more");
		CompletableFuture.allOf(hello1, hello2, hello3).thenRun(()->{
			try {
				hello1.get().close().addListener(ch->eventThread.shutdownGracefully());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		System.out.println("main thread ends");
	}
}
