package org.example.exmod.mixins.networking;

import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.netty.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.player.IProxPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NettyServer.class)
public class MixinNettyServer {

    @Inject(method = "removeContext", at = @At("TAIL"))
    private void removeContext(ChannelHandlerContext ctx, CallbackInfo ci, @Local Player removedPlayer) {
        try {
            ChannelHandlerContext udpContext = ((IProxPlayer) removedPlayer).getUDPContext();
            System.out.println(((IProxPlayer) removedPlayer).getUDPAddress() + " left prox-chat server");
            IProxNetIdentity identity = Server.identityMap.get(udpContext);
            Server.identityMap.remove(ctx);
            Server.identities.removeValue(identity, true);
        } catch (Exception ignore) {}
    }

}
