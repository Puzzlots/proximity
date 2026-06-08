package io.github.puzzlots.proximity.mixins.client;

import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.singletons.GameSingletonPlayers;
import io.github.puzzlots.proximity.io.audio.AudioPlaybackThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSingletonPlayers.class)
public class MixinPlayerSingletons {

    @Inject(method = "unregisterPlayer", at = @At("TAIL"))
    private static void unregisterPlayer(Player player, CallbackInfo ci) {
        AudioPlaybackThread.INSTANCE.removePlayer(player.getUsername());
    }

}
