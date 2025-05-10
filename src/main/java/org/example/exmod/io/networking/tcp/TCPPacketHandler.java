package org.example.exmod.io.networking.tcp;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.client.Client;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.serialization.KeylessBinaryDeserializer;
import org.example.exmod.util.BufferUtil;

import java.net.InetSocketAddress;
import java.net.SocketException;

public class TCPPacketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException && Constants.SIDE == EnvType.CLIENT) {
            System.out.println("Disconnected from Server");
            Client.shutdown();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (Constants.SIDE == EnvType.CLIENT) {
            Client.shutdown();
            super.channelInactive(ctx);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (Constants.SIDE != EnvType.SERVER) return;
        String address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();

        System.out.println("Handler Joined " + address);

        IProxNetIdentity identity = new TCPProxNetIdentity(ctx);
        Server.identityMap.put(ctx, identity);
        Server.identities.add(identity);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (Constants.SIDE != EnvType.SERVER) return;
        String address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        IProxNetIdentity identity = Server.identityMap.get(ctx);
        Server.identities.removeValue(identity, true);
        Server.identityMap.remove(ctx);

        System.out.println("Handler Left " + address);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = null;

        try {
            m = (ByteBuf)msg;

            short id = m.readShort();
            Class<? extends ProxPacket> packetClass = ProxPacket.PACKET_MAP.get((Short) id);
//            System.out.println("Packet Recieved " + id);
            if (packetClass == null) {
                m.release();
                return;
            }

            ProxPacket packet = packetClass.newInstance();
            packet.preRead(KeylessBinaryDeserializer.fromBytes(BufferUtil.toByteArray(m), true));
            packet.handle(Constants.SIDE, Server.identityMap.get(ctx));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
