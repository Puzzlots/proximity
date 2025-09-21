package io.github.puzzlots.proximity.io.audio;

import com.badlogic.gdx.math.Vector3;

public interface IAudioPlaybackThread extends Runnable {

    void queue(byte[] bytes, Vector3 position, Vector3 direction);
}
