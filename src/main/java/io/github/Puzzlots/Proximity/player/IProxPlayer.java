package io.github.Puzzlots.Proximity.player;

import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;

import java.net.InetSocketAddress;

public interface IProxPlayer {

    void setUdpAddress(InetSocketAddress address);
    InetSocketAddress getUDPAddress();

    void setUDPIdentity(IProxNetIdentity identity);
    IProxNetIdentity getUDPIdentity();

    boolean needsIdentity();
}
