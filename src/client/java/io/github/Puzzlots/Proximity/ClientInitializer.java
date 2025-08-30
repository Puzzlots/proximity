package io.github.Puzzlots.Proximity;

import dev.puzzleshq.puzzleloader.cosmic.core.modInitialises.ClientModInit;
import io.github.Puzzlots.Proximity.io.audio.AudioCaptureThread;
import io.github.Puzzlots.Proximity.io.audio.AudioPlaybackThread;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

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
