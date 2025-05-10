package org.example.exmod.io.audio;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Queue;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.ImmutablePair;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import de.maxhenkel.rnnoise4j.Denoiser;
import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import de.pottgames.tuningfork.*;
import de.pottgames.tuningfork.capture.CaptureConfig;
import de.pottgames.tuningfork.capture.CaptureDevice;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import org.example.exmod.io.networking.client.Client;
import org.example.exmod.io.networking.packets.PlayerReliantAudioPacket;
import org.example.exmod.io.networking.packets.ProxPacket;

import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioCaptureThread implements Runnable, IAudioCaptureThread {

    public static final Thread CAPTURE_THREAD;
    public static IAudioCaptureThread INSTANCE;
    public static final AtomicBoolean MIC_MUTED = new AtomicBoolean(false);
    public static Denoiser denoiser;

    static {
        CAPTURE_THREAD = new Thread(INSTANCE = new AudioCaptureThread(), "AUDIO-CAPTURE-THREAD");
        CAPTURE_THREAD.setDaemon(true);
        try {
            denoiser = new Denoiser();
        } catch (IOException | UnknownPlatformException e) {
            throw new RuntimeException(e);
        }
    }

    CaptureDevice device;

    public static boolean hasDevice() {
        return AudioCaptureThread.INSTANCE.hasCaptureDevice();
    }

    public static int getFrequency() {
        return INSTANCE.getDeviceFrequency();
    }

    public static void toggleMic() {
        AudioCaptureThread.MIC_MUTED.set(!AudioCaptureThread.MIC_MUTED.get());
    }

    @Override
    public void run() {
        int bufferSize = 4410;
        short[] buffer = new short[bufferSize];
//        ShortBuffer buffer = ShortBuffer.wrap(new short[bufferSize]);
//        ShortBuffer buffer = BufferUtils.newShortBuffer(bufferSize);
//        BufferUtils.newByteBuffer(bufferSize);

        System.out.println("CAPTURE START");
        final List<String> inputDeviceList = CaptureDevice.availableDevices();
        if (inputDeviceList == null || inputDeviceList.isEmpty()) {
            System.out.println("Error: no input device found");
        }

        for (String device1 : inputDeviceList) {
            System.out.println(device1);
        }

        final CaptureConfig config = new CaptureConfig();
        config.setPcmFormat(PcmFormat.MONO_16_BIT);
        config.setDeviceSpecifier(inputDeviceList.get(0));
        device = CaptureDevice.open(config);
        System.out.println("Device Name: " + device.getDeviceName());
//        source.queueSamples(buffer);
//        source.play();

//        final SoundEffect effect = new SoundEffect(new PitchShifter());
//        source.attachEffect(effect);

        boolean captureWasStopped = true;
        long sleepTime = System.currentTimeMillis();
        try {
            while (true) {
                if (device == null) continue;
                if (AudioCaptureThread.MIC_MUTED.get()) {
                    device.stopCapture();
                    captureWasStopped = true;
                    continue;
                }
                if (captureWasStopped) {
                    device.startCapture();
                    captureWasStopped = false;
                }

                while (device.capturedSamples() >= bufferSize) {
                    device.fetch16BitSamples(buffer, bufferSize);
//                    short[] denoisedBuffer = denoiser.denoise(buffer);

                    if (ClientNetworkManager.isConnected() && InGame.getLocalPlayer() != null && InGame.getLocalPlayer().getPosition() != null) {
//                        ProxPacket packet = new PlayerReliantAudioPacket(denoisedBuffer);
//                        Client.send(packet);

                        ProxPacket packet = new PlayerReliantAudioPacket(buffer);
                        Client.send(packet);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("CAPTURE END");

        if (!AudioCaptureThread.MIC_MUTED.get())
            device.stopCapture();
        device.dispose();
        denoiser.close();
    }

    public static void start() {
        AudioCaptureThread.CAPTURE_THREAD.start();
    }

    @Override
    public int getDeviceFrequency() {
        return device.getFrequency();
    }

    @Override
    public boolean hasCaptureDevice() {
        return device != null;
    }
}
