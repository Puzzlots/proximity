package io.github.Puzzlots.Proximity.io.audio;

import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.rnnoise4j.Denoiser;
import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import de.pottgames.tuningfork.*;
import de.pottgames.tuningfork.capture.CaptureConfig;
import de.pottgames.tuningfork.capture.CaptureDevice;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.settings.INumberSetting;
import finalforeach.cosmicreach.settings.types.FloatSetting;
import io.github.Puzzlots.Proximity.threading.ThreadBuilder;
import io.github.Puzzlots.Proximity.io.networking.client.Client;
import io.github.Puzzlots.Proximity.io.networking.packets.EncodedPlayerReliantAudioPacket;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;
import io.github.Puzzlots.Proximity.threading.Threads;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.Puzzlots.Proximity.util.AudioUtils.computeLevel;

public class AudioCaptureThread implements Runnable, IAudioCaptureThread {

    public static OpusEncoder encoder;
    public static IAudioCaptureThread INSTANCE;
    public static final AtomicBoolean MIC_MUTED = new AtomicBoolean(false);
    public static Denoiser denoiser;

    private static final AtomicBoolean LOOP_ACTIVE = new AtomicBoolean(true);

    public static final int rawSoundShortBufferSize = 480;
    public static INumberSetting micVolume = new FloatSetting("mic-volume", 1);

    public static float micLevel = 0;

    static {
        ThreadBuilder builder = ThreadBuilder.create("AUDIO-CAPTURE-THREAD", INSTANCE = new AudioCaptureThread());
        builder.setThreadDaemonState(true);
        Threads.AUDIO_CAPTURE_THREAD = builder.finish();

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

    public static void kill() {
        AudioCaptureThread.LOOP_ACTIVE.set(false);
    }

    public static void start() {
        Threads.AUDIO_CAPTURE_THREAD.start();
    }

    @Override
    public void run() {
        short[] buffer = new short[AudioCaptureThread.rawSoundShortBufferSize];
        try {
            encoder = new OpusEncoder(48000, 1, OpusEncoder.Application.LOW_DELAY);
            encoder.setMaxPayloadSize(1500);
        } catch (IOException | de.maxhenkel.opus4j.UnknownPlatformException e) {
            throw new RuntimeException(e);
        }

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

        boolean captureWasStopped = true;
        while (AudioCaptureThread.LOOP_ACTIVE.get()) {
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

            if (device.capturedSamples() < AudioCaptureThread.rawSoundShortBufferSize) continue;

            device.fetch16BitSamples(buffer, AudioCaptureThread.rawSoundShortBufferSize);
            short[] denoisedBuffer = denoiser.denoise(buffer);
            micLevel = computeLevel(denoisedBuffer);
            byte[] bytes = encoder.encode(denoisedBuffer);

            if (ClientNetworkManager.isConnected() && InGame.getLocalPlayer() != null && InGame.getLocalPlayer().getPosition() != null) {
                ProxPacket packet = new EncodedPlayerReliantAudioPacket(bytes);
                try {
                    Client.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!AudioCaptureThread.MIC_MUTED.get())
            device.stopCapture();
        device.dispose();
        denoiser.close();
        encoder.close();
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
