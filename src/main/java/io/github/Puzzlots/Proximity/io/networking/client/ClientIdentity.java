package io.github.Puzzlots.Proximity.io.networking.client;

import dev.puzzleshq.puzzleloader.loader.util.EnvType;
import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;

import java.io.IOException;

public class ClientIdentity implements IProxNetIdentity {

    @Override
    public void send(ProxPacket packet) throws IOException {
        Client.send(packet);
    }

    @Override
    public EnvType getSide() {
        return EnvType.CLIENT;
    }
}
