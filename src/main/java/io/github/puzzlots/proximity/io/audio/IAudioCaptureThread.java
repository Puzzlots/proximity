package io.github.puzzlots.proximity.io.audio;

public interface IAudioCaptureThread extends Runnable {

    int getDeviceFrequency();
    boolean hasCaptureDevice();
}
