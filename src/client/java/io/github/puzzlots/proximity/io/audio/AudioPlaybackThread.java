package io.github.puzzlots.proximity.io.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.entities.player.PlayerEntity;
import finalforeach.cosmicreach.settings.INumberSetting;
import finalforeach.cosmicreach.settings.types.FloatSetting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import io.github.puzzlots.proximity.threading.ThreadBuilder;
import io.github.puzzlots.proximity.threading.Threads;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static io.github.puzzlots.proximity.util.AudioUtils.computeLevel;

public class AudioPlaybackThread implements Runnable, IAudioPlaybackThread {

    public static IAudioPlaybackThread INSTANCE;
    public static OpusDecoder decoder;

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

    public void queue(byte[] bytes, Player player) {
//        System.out.println("queued Audio");
        PlayerEntity entity;
        if ((entity = (PlayerEntity) player.getEntity()) == null) return;
        ((IAudioPlayer)entity).play(bytes);
    }

    @Override
    public void run() {
        PcmSoundSource source = null;
        int queuePingCheck = 800000;

        try {
            decoder = new OpusDecoder(48000, 1);
        } catch (IOException | UnknownPlatformException e) {
            throw new RuntimeException(e);
        }
        decoder.setFrameSize(960);

        ShortBuffer buffer1 = BufferUtils.newShortBuffer(AudioCaptureThread.rawSoundShortBufferSize);
        try {
            boolean isDeviceUninitialized = true;
            while (isDeviceUninitialized) {
                Thread.sleep(1);
                if (!AudioCaptureThread.hasDevice()) continue;

                source = new PcmSoundSource(AudioCaptureThread.getFrequency(), PcmFormat.MONO_16_BIT);
                source.setVolume(30);
                source.enableAttenuation();
                source.makeDirectional(new Vector3(0, 1, 0), 22.5f, 45, .2f);
                source.setVirtualization(AudioConfig.Virtualization.ON);
                source.setSpatialization(AudioConfig.Spatialization.ON);
                source.setAttenuationMinDistance(0);
                source.setAttenuationMaxDistance(25);
                source.setRadius(20);
                isDeviceUninitialized = false;
            }
            System.out.println("Device has been initialized?: " + AudioCaptureThread.hasDevice());

            while (true) {
//                if (queue.isEmpty()) continue;
//
//                ImmutablePair<byte[], Player> info = queue.poll();
//
//                short[] shorts = decoder.decode(info.getLeft());
//                spkLevel = computeLevel(shorts);
//                buffer1.put(shorts);
//                buffer1.flip();
//                source.queueSamples(buffer1);
//                source.setDirection(info.getRight());
//                source.setPosition(info.getMiddle());
//                source.setVolume(AudioPlaybackThread.spkVolume.getValueAsFloat());
//                source.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        decoder.close();
    }

}
