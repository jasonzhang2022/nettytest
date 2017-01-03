package jason.example.discard1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardClientHandler extends ChannelInboundHandlerAdapter {

	public ChannelHandlerContext ctx;
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx1) throws Exception {
		ctx=ctx1;
		write("hello world").addListener(new ChannelFutureListener(){

			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.printf("client: send message when channel is active. success=%s\n", future.isSuccess()?"true":"false");
			}
			
		});
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	
	public ChannelFuture write(String msg) {
		byte[] array = msg.getBytes();
		ByteBuf buf = Unpooled.wrappedBuffer(array);
		buf.retain();
		System.out.printf("write %d characters :%s\n", array.length, msg);
		return ctx.writeAndFlush(buf);
	}

	

}
