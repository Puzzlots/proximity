package io.github.puzzlots.proximity.io.networking.protocol.udp;

import dev.puzzleshq.puzzleloader.loader.util.EnvType;
import finalforeach.cosmicreach.singletons.GameSingletons;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.github.puzzlots.proximity.io.networking.IProxNetIdentity;
import io.github.puzzlots.proximity.io.networking.packets.ProxPacket;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UDPProxNetIdentity implements IProxNetIdentity {

    protected InetSocketAddress address;
    protected ChannelHandlerContext context;

    public UDPProxNetIdentity(InetSocketAddress address, ChannelHandlerContext context) {
        this.context = context;
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    @Override
    public void send(ProxPacket packet) throws IOException {
        byte[] bytes = ProxPacket.setupToSend(packet);

        context.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(bytes), getAddress()));
    }

    public EnvType getSide() {
        return GameSingletons.isClient ? EnvType.CLIENT : EnvType.SERVER;
    }

}
