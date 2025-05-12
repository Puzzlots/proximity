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

public class AudioCaptureThread implements Runnable, IAudioCaptureThread {

    public static OpusEncoder encoder;
    public static IAudioCaptureThread INSTANCE;
    public static final AtomicBoolean MIC_MUTED = new AtomicBoolean(false);
    public static Denoiser denoiser;

    private static final AtomicBoolean LOOP_ACTIVE = new AtomicBoolean(true);

    public static final int rawSoundShortBufferSize = 480;
    public static INumberSetting micVolume = new FloatSetting("mic-volume", 1);

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

    public static void applyVolume(byte[] audio, float volume) {
        for (int i = 0; i < audio.length; i += 2) {
            short sample = (short) ((audio[i + 1] << 8) | (audio[i] & 0xFF));
            int scaledSample = (int) (sample * volume);

            // Clamp to 16-bit range to avoid overflow distortion
            if (scaledSample > Short.MAX_VALUE) scaledSample = Short.MAX_VALUE;
            if (scaledSample < Short.MIN_VALUE) scaledSample = Short.MIN_VALUE;

            audio[i] = (byte) (scaledSample & 0xFF);
            audio[i + 1] = (byte) ((scaledSample >> 8) & 0xFF);
        }
    }

    public static float computeLevel(byte[] pcmBytes) {
        int sampleCount = pcmBytes.length / 2;
        double sum = 0.0;

        for (int i = 0; i < pcmBytes.length - 1; i += 2) {
            // Little-endian: LSB first
            short sample = (short)((pcmBytes[i + 1] << 8) | (pcmBytes[i] & 0xFF));
            sum += sample * sample;
        }

        float rms = (float)Math.sqrt(sum / sampleCount);
        float db = 20f * (float)Math.log10(rms / 32768f + 1e-6f); // dBFS with epsilon
        db = Math.max(-60f, Math.min(0f, db)); // clamp between -60dB and 0dB

        float normalized = (db + 60f) / 60f; // normalize to 0.0 - 1.0
        return (float)Math.pow(normalized, 1.5); // perceptual curve
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
            byte[] bytes = encoder.encode(denoisedBuffer);
//            AudioCaptureThread.applyVolume(bytes, AudioCaptureThread.micVolume.getValueAsFloat());

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
