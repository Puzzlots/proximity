package org.example.exmod.io.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.ImmutablePair;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import de.maxhenkel.rnnoise4j.Denoiser;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import org.example.exmod.ThreadBuilder;
import org.example.exmod.threading.Threads;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioPlaybackThread implements Runnable, IAudioPlaybackThread {

    public static IAudioPlaybackThread INSTANCE;
    public static OpusDecoder decoder;

    static {
        ThreadBuilder builder = ThreadBuilder.create("AUDIO-PLAYBACK-THREAD", INSTANCE = new AudioPlaybackThread());
        builder.setThreadDaemonState(true);
        Threads.AUDIO_PLAYBACK_THREAD = builder.finish();
    }

    final Queue<Pair<byte[], Vector3>> queue = new ConcurrentLinkedQueue<>();

    public static void start() {
        Threads.AUDIO_PLAYBACK_THREAD.start();
    }

    public void queue(byte[] bytes, Vector3 location) {
        queue.add(new ImmutablePair<>(bytes, location));
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
            System.out.println("PLAYBACK START");
            boolean deviceSet = false;
            while (true) {
                if (AudioCaptureThread.hasDevice() && !deviceSet) {
                    System.out.println("FREQUENCY " + AudioCaptureThread.getFrequency());
                    source = new PcmSoundSource(AudioCaptureThread.getFrequency(), PcmFormat.MONO_16_BIT);
                    source.setSpatialization(AudioConfig.Spatialization.AUTO);
                    source.setVolume(30);
                    deviceSet = true;
                }
                if (!deviceSet) System.out.println(AudioCaptureThread.hasDevice());
                if (!AudioCaptureThread.hasDevice() && !deviceSet) {
                    continue;
                }

                while (!queue.isEmpty()) {
                    Pair<byte[], Vector3> info = queue.poll();
                    short[] shorts = decoder.decode(info.getLeft());
                    buffer1.put(shorts);
                    buffer1.flip();
                    source.queueSamples(buffer1);
                    source.setAttenuationMinDistance(0);
                    source.setAttenuationMaxDistance(118);
                    source.setPosition(info.getRight());
                    source.play();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        decoder.close();
        System.out.println("PLAYBACK END");
    }

}
