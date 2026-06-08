package io.github.puzzlots.proximity.io.audio;

import finalforeach.cosmicreach.entities.GameEntityUniqueId;

public interface IAudioCaptureThread extends Runnable {

    int getDeviceFrequency();
    boolean hasCaptureDevice();

}
