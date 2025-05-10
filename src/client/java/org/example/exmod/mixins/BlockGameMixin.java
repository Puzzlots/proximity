package org.example.exmod.mixins;

import finalforeach.cosmicreach.BlockGame;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import org.example.exmod.ProximityControls;
import org.example.exmod.VoiceMenu;
import org.example.exmod.io.audio.AudioCaptureThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static finalforeach.cosmicreach.gamestates.GameState.currentGameState;

@Mixin(BlockGame.class)
public abstract class BlockGameMixin {

    /**
     * <h3>Control hooking</h3>
     * <p>Injects into {@link finalforeach.cosmicreach.BlockGame#render()}  to check what keys are pressed</p>
     * @param ci Callback info for mixin
     */

    @Inject(method = "render", at = @At("HEAD"))
    public void onKey(CallbackInfo ci) {
        if (ProximityControls.toggleMute.isJustPressed() && currentGameState instanceof InGame) {
            AudioCaptureThread.toggleMic();
        }
        if (ProximityControls.openVoiceMenu.isJustPressed()) {
            if (currentGameState instanceof VoiceMenu) {
                GameState.switchToGameState(GameState.IN_GAME);
            } else if (currentGameState instanceof InGame) {
                GameState.switchToGameState(new VoiceMenu(false));
            }
        }
    }
}