package org.example.exmod.io.networking.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.networking.udp.DatagramProxChannelInitializer;
import org.example.exmod.io.networking.udp.UDPPacketHandler;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.example.exmod.io.serialization.KeylessBinarySerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    static EventLoopGroup clientGroup;
    public static Channel context;
    public static final AtomicBoolean shouldStartSendingAudio = new AtomicBoolean(false);
    public static final ClientIdentity IDENTITY = new ClientIdentity();

    public static void connect(String host, int port) throws InterruptedException {
        clientGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                .handler(new UDPPacketHandler());

        ChannelFuture future = bootstrap.connect(host, port).sync();
        Client.context = future.channel();
    }

    public static boolean isConnected() {
        return Client.context != null;
    }

    public static void shutdown() {
        shouldStartSendingAudio.set(false);
        clientGroup.shutdownGracefully();
        if (Client.context != null) Client.context.close();
        Client.context = null;
    }

    public static void send(ProxPacket packet) throws IOException {
        System.out.println(packet + " | " + Client.context);
        if (Client.context == null) return;

        IKeylessSerializer serializer = new KeylessBinarySerializer();
        packet.preWrite(serializer);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = serializer.toCompressedBytes();

        short id = ProxPacket.REVERSE_PACKET_MAP.get(packet.getClass());
        stream.write((byte) (id >>> 8));
        stream.write((byte) (id));
        stream.writeBytes(bytes);

        context.writeAndFlush(Server.useUDP ? Unpooled.wrappedBuffer(stream.toByteArray()) : stream.toByteArray());
    }

}
