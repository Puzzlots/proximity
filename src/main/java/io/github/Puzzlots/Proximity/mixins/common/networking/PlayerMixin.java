package io.github.Puzzlots.Proximity.mixins.common.networking;

import finalforeach.cosmicreach.entities.player.Player;
import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.player.IProxPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.net.InetSocketAddress;

@Mixin(Player.class)
public class PlayerMixin implements IProxPlayer {

    @Unique
    transient IProxNetIdentity proximity_audio$identity;
    @Unique
    transient InetSocketAddress proximity_audio$address;
    private boolean needsIdentity = true;

    @Override
    public void setUdpAddress(InetSocketAddress address) {
        this.proximity_audio$address = address;
    }

    @Override
    public InetSocketAddress getUDPAddress() {
        return this.proximity_audio$address;
    }

    @Override
    public void setUDPIdentity(IProxNetIdentity identity) {
        this.needsIdentity = false;

        this.proximity_audio$identity = identity;
    }

    @Override
    public IProxNetIdentity getUDPIdentity() {
        return this.proximity_audio$identity;
    }

    @Override
    public boolean needsIdentity() {
        return this.needsIdentity;
    }

}