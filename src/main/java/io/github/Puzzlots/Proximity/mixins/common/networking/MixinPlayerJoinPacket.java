package io.github.Puzzlots.Proximity.mixins.common.networking;

import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.packets.entities.PlayerPositionPacket;
import io.netty.channel.ChannelHandlerContext;
import io.github.Puzzlots.Proximity.io.networking.client.Client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerPositionPacket.class)
public class MixinPlayerJoinPacket {

    @Shadow public String playerUniqueId;

    @Inject(method = "handle", at = @At("TAIL"))
    private void handle(NetworkIdentity identity, ChannelHandlerContext ctx, CallbackInfo ci) {
        if (GameSingletons.isHost) {
            return;
        }

        if (!Objects.equals(this.playerUniqueId, GameSingletons.client().getAccount().getUniqueId())) return;

        Client.shouldStartSendingAudio.set(true);
    }

}
