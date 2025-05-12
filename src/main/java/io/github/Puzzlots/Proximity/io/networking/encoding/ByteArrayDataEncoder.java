package io.github.Puzzlots.Proximity.io.networking.encoding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ByteArrayDataEncoder extends MessageToByteEncoder<byte[]> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, byte[] msg, ByteBuf out) throws Exception {
        out.writeInt(msg.length);
        out.writeBytes(msg);
    }
}