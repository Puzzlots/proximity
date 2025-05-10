package org.example.exmod.io.networking.client;

import com.github.puzzle.core.loader.meta.EnvType;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.packets.ProxPacket;

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
