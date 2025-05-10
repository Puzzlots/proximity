package org.example.exmod.io.networking.packets;

import com.github.puzzle.core.loader.meta.EnvType;
import org.example.exmod.Constants;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NonLocationalAudioPacket extends ProxPacket {

    short[] shorts;

    public NonLocationalAudioPacket() {}

    public NonLocationalAudioPacket(short[] shorts) {
        this.shorts = shorts;
    }

    @Override
    public void read(IKeylessDeserializer deserializer) throws IOException {
        this.shorts = deserializer.readShortArrayAsNative();
    }

    @Override
    public void write(IKeylessSerializer serializer) throws IOException {
        serializer.writeShortArray(this.shorts);
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
        Constants.audioPlaybackThread.queue(shorts);
    }
}
