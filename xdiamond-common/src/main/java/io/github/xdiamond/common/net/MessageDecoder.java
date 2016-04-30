package io.github.xdiamond.common.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

  int state = 0;
  Message msg;
  int dataLength = 0;

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out)
      throws Exception {
    // * version + length + data(request/response)
    // * 2 + 4
    if (state == 0 && byteBuf.readableBytes() >= (2 + 4 + 2)) {
      msg = new Message();
      msg.setVersion(byteBuf.readShort());
      dataLength = byteBuf.readInt() - 2;
      msg.setType(byteBuf.readShort());

      state = 1;
    }
    if (state == 1 && byteBuf.readableBytes() >= dataLength) {
      byte[] data = new byte[dataLength];
      byteBuf.readBytes(data);
      msg.setData(data);
      out.add(msg);

      msg = null;
      dataLength = 0;
      state = 0;
    }
  }
}
