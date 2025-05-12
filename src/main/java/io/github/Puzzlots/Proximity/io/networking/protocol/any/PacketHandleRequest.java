package io.github.Puzzlots.Proximity.io.networking.protocol.any;

import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

import java.net.InetSocketAddress;

public record PacketHandleRequest(
        byte[] data,
        IProxNetIdentity identity,
        InetSocketAddress sender,
        Class<? extends ProxPacket> packetClass
) {}
