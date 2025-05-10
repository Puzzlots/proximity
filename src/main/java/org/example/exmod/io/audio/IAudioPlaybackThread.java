package org.example.exmod.io.audio;

import com.badlogic.gdx.math.Vector3;

public interface IAudioPlaybackThread extends Runnable {

    void queue(short[] shorts);
    void queue(short[] shorts, Vector3 location);

}
