package org.example.exmod.player;

import org.example.exmod.io.networking.IProxNetIdentity;

import java.net.InetSocketAddress;

public interface IProxPlayer {

    void setUdpAddress(InetSocketAddress address);
    InetSocketAddress getUDPAddress();

    void setUDPIdentity(IProxNetIdentity identity);
    IProxNetIdentity getUDPIdentity();

}
