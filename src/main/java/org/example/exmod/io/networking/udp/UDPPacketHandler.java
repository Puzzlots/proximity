package org.example.exmod.io.networking.udp;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import org.example.exmod.io.networking.client.Client;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.packets.ProxPacket;
import org.example.exmod.io.serialization.KeylessBinaryDeserializer;
import org.example.exmod.player.IProxPlayer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.SocketException;

public class UDPPacketHandler extends ChannelInboundHandlerAdapter {

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
            System.out.println("Disconnected from Server");
            Client.shutdown();
            super.channelInactive(ctx);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (Constants.SIDE != EnvType.SERVER) return;
//        String address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();

//        System.out.println("Handler Joined " + address);

//        ProxNetIdentity identity = new ProxNetIdentity(ctx);
//        Server.identityMap.put(ctx, identity);
//        Server.identities.add(identity);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        if (Constants.SIDE != EnvType.SERVER) return;
//        String address = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
//        ProxNetIdentity identity = Server.identityMap.get(ctx);
//        Server.identities.removeValue(identity, true);
//        Server.identityMap.remove(ctx);
//
//        System.out.println("Handler Left " + address);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagramPacket = null;
        ByteBuf byteBuf = null;
        DataInputStream dataInputStream;
        ByteArrayInputStream byteArrayInputStream;

        try {
            datagramPacket = (DatagramPacket)msg;
            byteBuf = datagramPacket.content();
            datagramPacket.retain();

            short id = byteBuf.readShort();
            Class<? extends ProxPacket> packetClass = ProxPacket.PACKET_MAP.get((Short) id);
//            System.out.println("Packet Received " + id);
            if (packetClass == null) {
                datagramPacket.release();
                byteBuf.release();
                return;
            }

            ProxPacket packet = packetClass.newInstance();
            packet.preRead(KeylessBinaryDeserializer.fromBytes(ByteBufUtil.getBytes(byteBuf), true));

            IProxPlayer player;
            if ((player = (IProxPlayer) GameSingletons.getPlayerFromUniqueId(packet.getPlayerUniqueId())) != null && player.needsContext()) {
                player.setUdpAddress(datagramPacket.sender());
                player.setUDPContext(ctx);
//                System.out.println(packet.getPlayerUniqueId());
//                System.out.println(GameSingletons.getPlayerFromUniqueId(packet.getPlayerUniqueId()));
//                System.out.println(Server.identityMap.get(ctx));
            }

            packet.handle(Constants.SIDE, Constants.SIDE.equals(EnvType.SERVER) ? Server.identityMap.get(ctx) : Client.IDENTITY);
            datagramPacket.release();
            byteBuf.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
