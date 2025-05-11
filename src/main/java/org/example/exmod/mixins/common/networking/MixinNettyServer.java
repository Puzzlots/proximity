package org.example.exmod.mixins.common.networking;

import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.netty.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.protocol.udp.UDPProxNetIdentity;
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
            IProxNetIdentity identity = ((IProxPlayer) removedPlayer).getUDPIdentity();

            if (Server.useUDP) {
                Server.identityMap.remove(Server.reverseIdentityMap.get(identity));
                Server.reverseIdentityMap.remove(identity);
                Server.SENDER_TO_IDENTITY_MAP.remove(((UDPProxNetIdentity) identity).getAddress());
                Server.identities.remove(identity);

                System.out.println(((IProxPlayer) removedPlayer).getUDPAddress() + " left prox-chat server");
            }
        } catch (Exception ignore) {}
    }

}
