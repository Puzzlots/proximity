package io.github.puzzlots.proximity.mixins.client;

import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.KeybindsMenu;
import finalforeach.cosmicreach.settings.Keybind;
import io.github.puzzlots.proximity.ProximityControls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeybindsMenu.class)
public abstract class KeybindingMenuMixin {

    @Inject(method = "<init>(Lfinalforeach/cosmicreach/gamestates/GameState;)V", at = @At("TAIL"))
    private void addKeybinds(GameState previousState, CallbackInfo ci) {
//        this.addKeybind("Mute", ProximityControls.toggleMute);
//        this.addKeybind("Open Voice Menu", ProximityControls.openVoiceMenu);
    }
}
