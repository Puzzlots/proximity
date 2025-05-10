package org.example.exmod.io.networking.packets;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.core.loader.meta.EnvType;
import org.example.exmod.Constants;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class LocationalAudioPacket extends ProxPacket {

    short[] shorts;
    Vector3 location;

    public LocationalAudioPacket() {}

    public LocationalAudioPacket(Vector3 location, short[] shorts) {
        this.shorts = shorts;
        this.location = location;
    }

    @Override
    public void read(IKeylessDeserializer deserializer) throws IOException {
        this.shorts = deserializer.readShortArrayAsNative();
        this.location = new Vector3();
        location.x = deserializer.readFloat();
        location.y = deserializer.readFloat();
        location.z = deserializer.readFloat();
    }

    @Override
    public void write(IKeylessSerializer serializer) throws IOException {
        serializer.writeShortArray(this.shorts);
        serializer.writeFloat(location.x);
        serializer.writeFloat(location.y);
        serializer.writeFloat(location.z);
    }

    @Override
    public void handle(EnvType type, @Nullable IProxNetIdentity proxNetIdentity) {
        if (type == EnvType.SERVER) {
            try {
                Server.broadcast(this, proxNetIdentity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        Constants.audioPlaybackThread.queue(shorts, location);
    }
}
