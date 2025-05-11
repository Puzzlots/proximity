package org.example.exmod.io.networking.protocol.any;

import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.packets.ProxPacket;

import java.net.InetSocketAddress;

public record PacketHandleRequest(
        byte[] data,
        IProxNetIdentity identity,
        InetSocketAddress sender,
        Class<? extends ProxPacket> packetClass
) {}
