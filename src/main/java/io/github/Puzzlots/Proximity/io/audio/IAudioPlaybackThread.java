package io.github.Puzzlots.Proximity.io.audio;

import com.badlogic.gdx.math.Vector3;

public interface IAudioPlaybackThread extends Runnable {

    void queue(byte[] bytes, Vector3 position, Vector3 direction);
}
