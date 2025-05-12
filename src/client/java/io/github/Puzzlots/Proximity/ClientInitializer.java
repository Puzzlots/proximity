package io.github.Puzzlots.Proximity;

import com.github.puzzle.core.loader.launch.provider.mod.entrypoint.impls.ClientModInitializer;
import io.github.Puzzlots.Proximity.io.audio.AudioCaptureThread;
import io.github.Puzzlots.Proximity.io.audio.AudioPlaybackThread;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

public class ClientInitializer implements ClientModInitializer {

    @Override
    public void onInit() {
        ProxPacket.register();
        AudioCaptureThread.start();
        AudioPlaybackThread.start();

        Constants.audioCaptureThread = AudioCaptureThread.INSTANCE;
        Constants.audioPlaybackThread = AudioPlaybackThread.INSTANCE;
    }

}
