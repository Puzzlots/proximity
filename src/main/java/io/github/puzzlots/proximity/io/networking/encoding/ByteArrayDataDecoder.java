package io.github.puzzlots.proximity.io.networking.encoding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteArrayDataDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Check if the initial 4 bytes are available to read the size
        if (in.readableBytes() < 4) return;

        in.markReaderIndex();

        int numBytes = in.readInt();

        if (in.readableBytes() < numBytes) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf buf = in.readBytes(numBytes);
        out.add(buf);
    }
}