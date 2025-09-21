package io.github.puzzlots.proximity.player;

import io.github.puzzlots.proximity.io.networking.IProxNetIdentity;

import java.net.InetSocketAddress;

public interface IProxPlayer {

    void setUdpAddress(InetSocketAddress address);
    InetSocketAddress getUDPAddress();

    void setUDPIdentity(IProxNetIdentity identity);
    IProxNetIdentity getUDPIdentity();

    boolean needsIdentity();
}
