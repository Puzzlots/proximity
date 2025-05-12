package io.github.Puzzlots.Proximity.mixins.client;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.Viewport;
import finalforeach.cosmicreach.rendering.GameTexture;
import finalforeach.cosmicreach.ui.UI;
import io.github.Puzzlots.Proximity.Constants;
import io.github.Puzzlots.Proximity.VoiceMenu;
import io.github.Puzzlots.Proximity.io.audio.AudioCaptureThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UI.class)
public abstract class UIMixin {
    @Shadow
    private Viewport uiViewport;

    /**
     * <h3>Drawing mic icon</h3>
     * <p>Injects into {@link UI#render()} to draw the microphone icon</p>
     * @param ci Callback info for mixin
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void drawIcon(CallbackInfo ci) {
        Texture micOn = GameTexture.load(Constants.MOD_ID + ":status/mic.png").get();
        Texture micOff = GameTexture.load(Constants.MOD_ID + ":status/mic_off.png").get();

        if (UI.renderUI && VoiceMenu.drawIcon) {
            //swapping to correct texture
            Sprite statusIcon = new Sprite(micOn);
            if (AudioCaptureThread.MIC_MUTED.get()) {statusIcon.setTexture(micOff);}

            //set positions
            statusIcon.flip(false,true);
            statusIcon.setPosition(-uiViewport.getWorldWidth()/2+3, (uiViewport.getWorldHeight()/2) -32 -3);

            //drawing to screen
            UI.batch.setProjectionMatrix(this.uiViewport.getCamera().combined);
            UI.batch.begin();
            VoiceMenu.initText();
            statusIcon.draw(UI.batch);
            UI.batch.end();
        }
    }
}