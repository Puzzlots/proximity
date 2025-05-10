package org.example.exmod.io.networking;

import com.github.puzzle.core.loader.meta.EnvType;
import org.example.exmod.io.networking.packets.ProxPacket;

import java.io.IOException;

public interface IProxNetIdentity {

    void send(ProxPacket packet) throws IOException;
    EnvType getSide();

}
