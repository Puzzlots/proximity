package org.example.exmod.io.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.networking.protocol.udp.DatagramProxChannelInitializer;
import org.example.exmod.util.FixedArray;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    public static final int RECV_BUFF_SIZE = 65535;
    public static final boolean useUDP = true;

    static EventLoopGroup mainGroup;
    static ChannelGroup channelGroup;

    public static final FixedArray<IProxNetIdentity> identities = new FixedArray<>();
    public static final Map<ChannelHandlerContext, IProxNetIdentity> identityMap = new ConcurrentHashMap<>();
    public static final Map<IProxNetIdentity, ChannelHandlerContext> reverseIdentityMap = new ConcurrentHashMap<>();

    public static final Map<InetSocketAddress, IProxNetIdentity> SENDER_TO_IDENTITY_MAP = new ConcurrentHashMap<>();

    public static void start(int port) throws InterruptedException {
        Server.identities.clear();
        Server.identityMap.clear();
        Server.reverseIdentityMap.clear();

        mainGroup = new NioEventLoopGroup();
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(mainGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.AUTO_CLOSE, true)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(Server.RECV_BUFF_SIZE))
                    .handler(new DatagramProxChannelInitializer());

            ChannelFuture future = bootstrap.bind("0.0.0.0", port).sync();
            channelGroup.add(future.channel());
        } catch (Exception e) {
            shutdown();
        }
    }

    public static void shutdown() {
        channelGroup.clear();
        mainGroup.shutdownGracefully();
    }

    public static void broadcast(ProxPacket packet, IProxNetIdentity proxNetIdentity) throws IOException {
        for (IProxNetIdentity identity : Server.identities) {
            if (identity != proxNetIdentity)
                identity.send(packet);
        }
    }

    public static void broadcast(ProxPacket packet) throws IOException {
        for (IProxNetIdentity identity : Server.identities) {
            identity.send(packet);
        }
    }
}
