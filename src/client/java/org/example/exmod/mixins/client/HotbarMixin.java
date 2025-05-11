package org.example.exmod.mixins.client;

import finalforeach.cosmicreach.items.Hotbar;
import finalforeach.cosmicreach.items.ISlotContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hotbar.class)
public abstract class HotbarMixin {

    @Shadow public abstract short getSelectedSlotNum();

    @Shadow private ISlotContainer container;

    @Inject(method = "scrolled", at=@At(value = "RETURN"))
    private void onSoc(float amountX, float amountY, CallbackInfoReturnable<Boolean> cir) {
        if(this.container == null) return;
    }
}