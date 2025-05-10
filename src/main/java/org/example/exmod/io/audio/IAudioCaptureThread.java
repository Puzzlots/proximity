package org.example.exmod.io.audio;

import com.badlogic.gdx.math.Vector3;

public interface IAudioCaptureThread extends Runnable {

    int getDeviceFrequency();
    boolean hasCaptureDevice();
}
