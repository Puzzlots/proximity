package org.example.exmod.player;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public interface IProxPlayer {

    void setUdpAddress(InetSocketAddress address);
    InetSocketAddress getUDPAddress();

    void setUDPContext(ChannelHandlerContext context);
    ChannelHandlerContext getUDPContext();

    boolean needsContext();

}
