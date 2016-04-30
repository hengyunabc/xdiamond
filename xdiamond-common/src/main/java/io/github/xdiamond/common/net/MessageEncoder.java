package io.github.xdiamond.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
    // * version + length + type + data(request/response)
    // * 2 + 4 + 2, length = data.length + 2
    out.writeShort(msg.getVersion());
    out.writeInt(msg.getData().length + 2);
    out.writeShort(msg.getType());
    out.writeBytes(msg.getData());
    ctx.flush();
  }
}
