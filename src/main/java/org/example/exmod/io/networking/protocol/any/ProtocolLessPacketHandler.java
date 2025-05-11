package org.example.exmod.io.networking.protocol.any;

import io.netty.buffer.ByteBuf;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.packets.ProxPacket;

import java.net.InetSocketAddress;

public class ProtocolLessPacketHandler {

    public static void handle(ByteBuf byteBuf, Runnable dataReleaseTrigger, IProxNetIdentity identity, InetSocketAddress socketAddress) {
        short packetId = byteBuf.readShort();

        Class<? extends ProxPacket> packetClass = ProxPacket.PACKET_MAP.get(packetId);
        System.out.println(packetClass);
        if (packetClass == null) {
            System.err.println("Unknown packet with id \"" + packetId + "\" and readable length of \"" + byteBuf.readableBytes() + "\".");
            dataReleaseTrigger.run();
            return;
        }
        int packetDataSize = byteBuf.readInt();

        byte[] packetData = new byte[packetDataSize];
        byteBuf.readBytes(packetData, 0, packetData.length);
        PacketHandleRequest request = new PacketHandleRequest(
                packetData,
                identity,
                socketAddress,
                packetClass
        );

        PacketHandlingThread.queue(request);
        dataReleaseTrigger.run();
    }

}
