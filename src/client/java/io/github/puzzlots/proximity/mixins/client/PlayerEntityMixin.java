package io.github.puzzlots.proximity.mixins.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import finalforeach.cosmicreach.entities.GameEntity;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import io.github.puzzlots.proximity.io.audio.AudioCaptureThread;
import io.github.puzzlots.proximity.io.audio.AudioPlaybackThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ShortBuffer;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends GameEntity {

    @Unique
    private transient PcmSoundSource source;
    @Unique
    private transient ShortBuffer buffer1;

    public PlayerEntityMixin(String entityTypeId) {
        super(entityTypeId);
    }


}
