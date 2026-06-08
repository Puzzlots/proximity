package io.github.puzzlots.proximity.io.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import finalforeach.cosmicreach.entities.GameEntityUniqueId;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.settings.types.FloatSetting;
import finalforeach.cosmicreach.util.settings.types.INumberSetting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import io.github.puzzlots.proximity.threading.ThreadBuilder;
import io.github.puzzlots.proximity.threading.Threads;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static io.github.puzzlots.proximity.util.AudioUtils.computeLevel;

public class AudioPlaybackThread implements Runnable, IAudioPlaybackThread {

    public static IAudioPlaybackThread INSTANCE;

    static {
        ThreadBuilder builder = ThreadBuilder.create("AUDIO-PLAYBACK-THREAD", INSTANCE = new AudioPlaybackThread());
        builder.setThreadDaemonState(true);
        Threads.AUDIO_PLAYBACK_THREAD = builder.finish();
    }

    public static INumberSetting spkVolume = new FloatSetting("speaker-volume", 100);

    public static float spkLevel = 0;

    public static void start() {
        Threads.AUDIO_PLAYBACK_THREAD.start();
    }

    public static final Queue<Pair<byte[], PlayerEntity>> queue = new ConcurrentLinkedQueue<>();

    public void queue(byte[] bytes, Player player) {
//        System.out.println("queued Audio");
        PlayerEntity entity;
        if ((entity = (PlayerEntity) player.getEntity()) == null) return;
        queue.add(new ImmutablePair<>(bytes, entity));
//        ((IAudioPlayer)entity).play(bytes);
    }

    // Track per-player state
    private static class PlayerAudioState {
        final OpusDecoder decoder;
        final PcmSoundSource source;
        final ShortBuffer buffer;

        PlayerAudioState(int frequency) throws IOException, UnknownPlatformException {
            decoder = new OpusDecoder(48000, 1);
            decoder.setFrameSize(960);
            buffer = BufferUtils.newShortBuffer(AudioCaptureThread.rawSoundShortBufferSize);

            source = new PcmSoundSource(frequency, PcmFormat.MONO_16_BIT);
            source.setVolume(100);
            source.enableAttenuation();
            source.makeDirectional(new Vector3(0, 1, 0), 180f, 360f, 0.8f);
            source.setVirtualization(AudioConfig.Virtualization.ON);
            source.setSpatialization(AudioConfig.Spatialization.ON);
            source.setAttenuationMinDistance(0);
            source.setAttenuationMaxDistance(25);
            source.setRadius(20);
        }

        float agcGain    = 1.0f;
        float limiterEnv = 1.0f;

        static final float TARGET_LEVEL        = 3000f;
        static final float AGC_SPEED           = 0.02f;
        static final float MAX_GAIN            = 8.0f;
        static final float MIN_GAIN            = 0.5f;
        static final float LIMITER_THRESHOLD   = 28000f;
        static final float LIMITER_RELEASE     = 0.001f;

        short[] processAudio(short[] shorts) {
            float rms = computeLevel(shorts);

            if (rms > 0) {
                float desired = TARGET_LEVEL / rms;
                agcGain += (desired - agcGain) * AGC_SPEED;
                agcGain = Math.clamp(agcGain, MIN_GAIN, MAX_GAIN);
            }

            for (int i = 0; i < shorts.length; i++) {
                float sample = shorts[i] * agcGain;
                float absS = Math.abs(sample);

                if (absS > LIMITER_THRESHOLD) {
                    limiterEnv = LIMITER_THRESHOLD / absS;
                } else {
                    limiterEnv = Math.min(1.0f, limiterEnv + LIMITER_RELEASE);
                }

                shorts[i] = (short) Math.clamp(sample * limiterEnv, Short.MIN_VALUE, Short.MAX_VALUE);
            }

            return shorts;
        }

        void close() {
            decoder.close();
            source.dispose();
        }
    }

    // Keyed by player UUID
    private final Map<String, PlayerAudioState> playerStates = new ConcurrentHashMap<>();

    private PlayerAudioState getOrCreateState(PlayerEntity entity, int frequency) {
        return playerStates.computeIfAbsent(entity.getPlayer().getUsername(), id -> {
            try {
                return new PlayerAudioState(frequency);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void run() {
        while (!AudioCaptureThread.hasDevice()) {
            try { Thread.sleep(1); } catch (InterruptedException e) { return; }
        }

        int frequency = AudioCaptureThread.getFrequency();
        System.out.println("Device initialized: " + AudioCaptureThread.hasDevice());

        while (true) {
            if (queue.isEmpty()) {
                Thread.onSpinWait();
                continue;
            }

            Pair<byte[], PlayerEntity> info = queue.poll();
            if (info == null) continue;

            PlayerEntity entity = info.getRight();
            if (entity.zone != InGame.getLocalPlayer().getZone()) continue;

            try {
                PlayerAudioState state = getOrCreateState(entity, frequency);

                short[] shorts = state.decoder.decode(info.getLeft());
                spkLevel = computeLevel(shorts);
                state.processAudio(shorts);

                state.buffer.clear();
                state.buffer.put(shorts);
                state.buffer.flip();

                state.source.queueSamples(state.buffer);
                state.source.setDirection(entity.viewDirection);
                state.source.setPosition(entity.position);
                state.source.setVolume(spkVolume.getValueAsFloat() * 100);
                state.source.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Call this when a player disconnects to avoid leaking resources
    public void removePlayer(String playerUserName) {
        PlayerAudioState state = playerStates.remove(playerUserName);
        if (state != null) state.close();
    }

}
