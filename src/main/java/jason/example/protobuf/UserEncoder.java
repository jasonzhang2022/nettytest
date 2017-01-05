package jason.example.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class UserEncoder extends MessageToByteEncoder<User> {

	Schema<User> schema = null;
	LinkedBuffer buffer = LinkedBuffer.allocate();
	@Override
	protected void encode(ChannelHandlerContext ctx, User msg, ByteBuf out) throws Exception {
		super.handlerAdded(ctx);
		
		byte[] protobuf = ProtobufIOUtil.toByteArray(msg, schema, buffer);
		System.out.println(ByteBufUtil.hexDump(protobuf));
		out.writeBytes(protobuf);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		schema =  RuntimeSchema.getSchema(User.class);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}

}
