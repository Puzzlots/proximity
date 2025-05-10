package org.example.exmod.io.networking.packets;

import com.github.puzzle.core.loader.meta.EnvType;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;

import java.io.IOException;

public class MessagePacket extends ProxPacket {

    String message;

    public MessagePacket() {}

    public MessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void read(IKeylessDeserializer deserializer) throws IOException {
        message = deserializer.readString();
    }

    @Override
    public void write(IKeylessSerializer serializer) throws IOException {
        serializer.writeString(message);
    }

    @Override
    public void handle(EnvType type, IProxNetIdentity proxNetIdentity) {
        System.out.println(message);
    }

}
