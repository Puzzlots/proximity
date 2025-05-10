package org.example.exmod.io.networking;

import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.netty.NettyServer;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.server.ServerLauncher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.networking.udp.DatagramProxChannelInitializer;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.util.FixedArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static EventLoopGroup mainGroup;
    static ChannelGroup channelGroup;

    public static FixedArray<IProxNetIdentity> identities = new FixedArray<>();
    public static Map<ChannelHandlerContext, IProxNetIdentity> identityMap = new HashMap<>();

    public static final boolean useUDP = true;

    public static void start(int port) throws InterruptedException {
        Server.identities.clear();
        Server.identityMap.clear();

        mainGroup = new NioEventLoopGroup();
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(mainGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.AUTO_CLOSE, true)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                    .handler(new DatagramProxChannelInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();
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
        System.out.println(Server.identities);
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
