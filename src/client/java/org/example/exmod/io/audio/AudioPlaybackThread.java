package org.example.exmod.io.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Queue;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.ImmutablePair;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import de.pottgames.tuningfork.AudioConfig;
import de.pottgames.tuningfork.PcmFormat;
import de.pottgames.tuningfork.PcmSoundSource;
import de.pottgames.tuningfork.capture.CaptureDevice;

import java.nio.ShortBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioPlaybackThread implements Runnable, IAudioPlaybackThread {

    public static final Thread PLAYBACK_THREAD;
    public static IAudioPlaybackThread INSTANCE;

    static {
        PLAYBACK_THREAD = new Thread(INSTANCE = new AudioPlaybackThread(), "AUDIO-PLAYBACK-THREAD");
        PLAYBACK_THREAD.setDaemon(true);
    }

    final Queue<Object> queue = new Queue<>();

    @Override
    public void queue(short[] shorts) {
        System.out.println("Queue");
        queue.addLast(shorts);
    }

    public void queue(short[] bytes, Vector3 location) {
        System.out.println("Queue");
        queue.addLast(new ImmutablePair<>(bytes, location));
    }

    @Override
    public void run() {
        int bufferSize = 4410;

        PcmSoundSource source = null;

        try {
            System.out.println("PLAYBACK START");
            boolean deviceSet = false;
            while (true) {
                if (AudioCaptureThread.hasDevice() && !deviceSet) {
                    source = new PcmSoundSource(AudioCaptureThread.getFrequency(), PcmFormat.MONO_16_BIT);
                    source.setSpatialization(AudioConfig.Spatialization.AUTO);
                    source.setVolume(30);
                    deviceSet = true;
                }
                System.out.println(AudioCaptureThread.hasDevice());
                if (!AudioCaptureThread.hasDevice() && !deviceSet) {
//                    System.out.println(AudioCaptureThread.hasDevice() + " " + deviceSet);
                    continue;
                }
//                System.out.println("AAA");
//                System.out.println("A " + queue.size);

                while (!queue.isEmpty()) {
                    Object object = queue.removeFirst();
                    short[] shorts = null;
                    boolean is2D = false;
                    Vector3 position = null;
                    if (object instanceof short[]) {
                        shorts = (short[]) object;
                        is2D = true;
                    }
                    if (object instanceof Pair pair) {
                        shorts = (short[]) pair.getLeft();
                        position = (Vector3) pair.getRight();
                    }
                    ShortBuffer buffer1 = BufferUtils.newShortBuffer(bufferSize);
                    buffer1.put(shorts);
                    buffer1.flip();
                    source.queueSamples(buffer1);
                    if (!is2D) {
                        source.enableAttenuation();
                        source.setAttenuationMinDistance(0);
                        source.setAttenuationMaxDistance(118);
                        source.setPosition(position);
                    } else {
                        source.disableAttenuation();
                    }
                    source.play();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PLAYBACK END");
    }

    public static void start() {
        AudioPlaybackThread.PLAYBACK_THREAD.start();
    }

}
