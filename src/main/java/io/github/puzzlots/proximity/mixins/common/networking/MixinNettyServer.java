package io.github.puzzlots.proximity.mixins.common.networking;

import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.netty.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.github.puzzlots.proximity.io.networking.IProxNetIdentity;
import io.github.puzzlots.proximity.io.networking.Server;
import io.github.puzzlots.proximity.io.networking.protocol.udp.UDPProxNetIdentity;
import io.github.puzzlots.proximity.player.IProxPlayer;
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
                Server.identities.removeValue(identity, true);
            }
        } catch (Exception ignore) {}
    }
}
