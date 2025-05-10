package org.example.exmod.mixins;

import finalforeach.cosmicreach.entities.player.Player;
import io.netty.channel.ChannelHandlerContext;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.networking.udp.UDPProxNetIdentity;
import org.example.exmod.player.IProxPlayer;
import org.spongepowered.asm.mixin.Mixin;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Mixin(Player.class)
public class PlayerMixin implements IProxPlayer {

    transient ChannelHandlerContext context;
    transient InetSocketAddress address;

    transient boolean needsContext = true;

    @Override
    public void setUdpAddress(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public InetSocketAddress getUDPAddress() {
        return this.address;
    }

    @Override
    public void setUDPContext(ChannelHandlerContext context) {
        this.context = context;
        this.needsContext = false;

        UDPProxNetIdentity identity = new UDPProxNetIdentity(address, context);
        Server.identityMap.put(context, identity);
        Server.identities.add(identity);

        System.out.println(address.toString() + " connected to prox-chat server");
    }

    @Override
    public ChannelHandlerContext getUDPContext() {
        return this.context;
    }

    @Override
    public boolean needsContext() {
        return this.needsContext;
    }
}
