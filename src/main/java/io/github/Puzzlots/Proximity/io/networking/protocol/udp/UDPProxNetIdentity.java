package io.github.Puzzlots.Proximity.io.networking.protocol.udp;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

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
        return Constants.SIDE;
    }

}
