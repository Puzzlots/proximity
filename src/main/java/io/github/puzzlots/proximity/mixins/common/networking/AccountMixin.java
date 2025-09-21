package io.github.puzzlots.proximity.mixins.common.networking;

import finalforeach.cosmicreach.accounts.Account;
import io.github.puzzlots.proximity.io.networking.IProxNetIdentity;
import io.github.puzzlots.proximity.player.IProxPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.net.InetSocketAddress;

@Mixin(Account.class)
public class AccountMixin implements IProxPlayer {

    @Unique
    transient IProxNetIdentity proximity_audio$identity;
    @Unique
    transient InetSocketAddress proximity_audio$address;
    @Unique
    private boolean proximity$needsIdentity = true;

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
        this.proximity$needsIdentity = false;

        this.proximity_audio$identity = identity;
    }

    @Override
    public IProxNetIdentity getUDPIdentity() {
        return this.proximity_audio$identity;
    }

    @Override
    public boolean needsIdentity() {
        return this.proximity$needsIdentity;
    }

}