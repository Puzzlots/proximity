package org.example.exmod.io.networking.tcp;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import io.netty.channel.ChannelHandlerContext;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.example.exmod.io.serialization.KeylessBinarySerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TCPProxNetIdentity implements IProxNetIdentity {

    protected ChannelHandlerContext context;

    public TCPProxNetIdentity(ChannelHandlerContext context) {
        this.context = context;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void send(ProxPacket packet) throws IOException {
        IKeylessSerializer serializer = new KeylessBinarySerializer();
        packet.preWrite(serializer);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = serializer.toCompressedBytes();

        short id = ProxPacket.REVERSE_PACKET_MAP.get(packet.getClass());
        stream.write((byte) (id >>> 8));
        stream.write((byte) (id));
        stream.writeBytes(bytes);

        context.writeAndFlush(stream.toByteArray());
    }

    public EnvType getSide() {
        return Constants.SIDE;
    }

}
