package org.example.exmod.io.networking.client;

import finalforeach.cosmicreach.GameSingletons;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.packets.MessagePacket;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.networking.protocol.udp.UDPPacketHandler;

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
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(Server.RECV_BUFF_SIZE))
                .handler(new UDPPacketHandler());

        ChannelFuture future = bootstrap.connect(host, port).addListener((f) -> {
            if (f.isSuccess()) {
                System.out.println("Connected to server \"" + host + ":" + port + "\"");
                Client.send(new MessagePacket(GameSingletons.client().getAccount().getDebugString() + " has joined the game."));
            } else System.out.println("Could not connect to server.");
        }).sync();
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
//        System.out.println(packet + " | " + Client.context);
        if (Client.context == null) return;

        byte[] bytes = ProxPacket.setupToSend(packet);

//        System.out.println(bytes.length);
        context.writeAndFlush(Server.useUDP ? Unpooled.wrappedBuffer(bytes) : bytes);
    }

}
