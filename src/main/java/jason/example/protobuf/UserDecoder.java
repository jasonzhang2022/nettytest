package jason.example.protobuf;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class UserDecoder extends SimpleChannelInboundHandler<ByteBuf> {
	
	
	Schema<User> schema = null;

	
	@Override
	public boolean isSharable() {
		return false;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		schema =  RuntimeSchema.getSchema(User.class);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		int len = msg.readableBytes()-2;
		msg.readByte();
		msg.readByte();
		
		User user = schema.newMessage();
		byte[] bytes = new byte[len];
		msg.readBytes(bytes);

		System.out.println(ByteBufUtil.hexDump(bytes));
		ProtobufIOUtil.mergeFrom(bytes, user, schema);
		ctx.fireChannelRead(user);
	}
	
}
