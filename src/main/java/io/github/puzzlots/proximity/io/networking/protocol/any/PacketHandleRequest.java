package io.github.puzzlots.proximity.io.networking.protocol.any;

import io.github.puzzlots.proximity.io.networking.IProxNetIdentity;
import io.github.puzzlots.proximity.io.networking.packets.ProxPacket;

import java.net.InetSocketAddress;

public record PacketHandleRequest(
        byte[] data,
        IProxNetIdentity identity,
        InetSocketAddress sender,
        Class<? extends ProxPacket> packetClass
) {}
