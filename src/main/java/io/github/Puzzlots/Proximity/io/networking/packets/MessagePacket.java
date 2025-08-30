package io.github.Puzzlots.Proximity.io.networking.packets;

import dev.puzzleshq.puzzleloader.loader.util.EnvType;
import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.io.serialization.IKeylessDeserializer;
import io.github.Puzzlots.Proximity.io.serialization.IKeylessSerializer;

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
