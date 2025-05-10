package org.example.exmod.io.networking.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.example.exmod.io.serialization.KeylessBinarySerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class UDPProxNetIdentity extends TCPProxNetIdentity {

    InetSocketAddress address;

    public UDPProxNetIdentity(InetSocketAddress address, ChannelHandlerContext context) {
        super(context);
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public void send(ProxPacket packet) throws IOException {
        IKeylessSerializer serializer = new KeylessBinarySerializer();
        packet.preWrite(serializer);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = serializer.toCompressedBytes();

        short id = ProxPacket.REVERSE_PACKET_MAP.get(packet.getClass());
        stream.write((byte) (id >>> 8));
        stream.write((byte) (id));
        stream.writeBytes(bytes);

        context.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(stream.toByteArray()), getAddress()));
    }

}
