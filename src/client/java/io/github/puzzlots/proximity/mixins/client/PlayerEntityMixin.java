package io.github.puzzlots.proximity.mixins.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import io.github.puzzlots.proximity.io.audio.AudioCaptureThread;
import io.github.puzzlots.proximity.io.audio.AudioPlaybackThread;
import io.github.puzzlots.proximity.io.audio.IAudioPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ShortBuffer;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin extends Entity implements IAudioPlayer {

    @Unique
    private transient PcmSoundSource source;
    @Unique
    private transient ShortBuffer buffer1;

    public PlayerEntityMixin(String entityTypeId) {
        super(entityTypeId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo info) {
        source = new PcmSoundSource(AudioCaptureThread.getFrequency(), PcmFormat.MONO_16_BIT);
        source.setVolume(30);
        source.enableAttenuation();
        source.makeDirectional(new Vector3(0, 1, 0), 22.5f, 45, .2f);
        source.setVirtualization(AudioConfig.Virtualization.ON);
        source.setSpatialization(AudioConfig.Spatialization.ON);
        source.setAttenuationMinDistance(0);
        source.setAttenuationMaxDistance(25);
        source.setRadius(20);
        buffer1 = BufferUtils.newShortBuffer(AudioCaptureThread.rawSoundShortBufferSize);
    }

    @Unique
    public void play(byte[] data) {
        short[] shorts = AudioPlaybackThread.decoder.decode(data);
//        spkLevel = computeLevel(shorts);
        buffer1.put(shorts);
        buffer1.flip();
        source.queueSamples(buffer1);
        source.setDirection(viewDirection);
        source.setPosition(position);
        source.setVolume(AudioPlaybackThread.spkVolume.getValueAsFloat());
        source.play();
    }

}
