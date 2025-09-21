package io.github.puzzlots.proximity;

import dev.puzzleshq.puzzleloader.cosmic.core.modInitialises.ClientModInit;
import io.github.puzzlots.proximity.io.audio.AudioCaptureThread;
import io.github.puzzlots.proximity.io.audio.AudioPlaybackThread;
import io.github.puzzlots.proximity.io.networking.packets.ProxPacket;

public class ClientInitializer implements ClientModInit {

    @Override
    public void onClientInit() {
        ProxPacket.register();
        AudioCaptureThread.start();
        AudioPlaybackThread.start();

        Constants.audioCaptureThread = AudioCaptureThread.INSTANCE;
        Constants.audioPlaybackThread = AudioPlaybackThread.INSTANCE;
    }

}
