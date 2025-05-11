package org.example.exmod.io.networking.protocol.udp;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.client.Client;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.protocol.any.ProtocolLessPacketHandler;

import java.net.SocketException;

public class UDPPacketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException && Constants.SIDE == EnvType.CLIENT) {
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagramPacket = (DatagramPacket)msg;
        ByteBuf byteBuf = datagramPacket.content();

        try {
            datagramPacket.retain();

            IProxNetIdentity identity = Constants.SIDE == EnvType.CLIENT ? Client.IDENTITY : Server.SENDER_TO_IDENTITY_MAP.get(datagramPacket.sender());
            if (Constants.SIDE == EnvType.SERVER && identity == null) {
                identity = new UDPProxNetIdentity(datagramPacket.sender(), ctx);
                Server.identityMap.put(ctx, identity);
                Server.reverseIdentityMap.put(identity, ctx);
                Server.SENDER_TO_IDENTITY_MAP.put(datagramPacket.sender(), identity);
                Server.identities.add(identity);
            }

            ProtocolLessPacketHandler.handle(byteBuf, () -> {
                datagramPacket.release();
                byteBuf.release();
            }, identity, datagramPacket.sender());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
