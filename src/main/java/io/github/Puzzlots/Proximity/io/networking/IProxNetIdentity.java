package io.github.Puzzlots.Proximity.io.networking;

import dev.puzzleshq.puzzleloader.loader.util.EnvType;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

import java.io.IOException;

public interface IProxNetIdentity {

    void send(ProxPacket packet) throws IOException;
    EnvType getSide();

}
