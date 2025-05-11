package org.example.exmod.mixins.client;

import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.KeybindsMenu;
import finalforeach.cosmicreach.settings.Keybind;
import org.example.exmod.ProximityControls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeybindsMenu.class)
public abstract class KeybindingMenuMixin {
    @Shadow protected abstract void addKeybindButton(String label, Keybind keybind);

    @Inject(method = "<init>(Lfinalforeach/cosmicreach/gamestates/GameState;)V", at = @At("TAIL"))
    private void addKeybinds(GameState previousState, CallbackInfo ci) {
        this.addKeybindButton("Mute", ProximityControls.toggleMute);
        this.addKeybindButton("Open Voice Menu", ProximityControls.openVoiceMenu);
    }
}
