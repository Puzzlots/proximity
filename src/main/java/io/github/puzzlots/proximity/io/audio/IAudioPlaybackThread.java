package io.github.puzzlots.proximity.io.audio;

import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.entities.player.Player;

public interface IAudioPlaybackThread extends Runnable {

    void queue(byte[] bytes, Player player);
}
