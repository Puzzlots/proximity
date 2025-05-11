package org.example.exmod.io.networking.packets;

import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.player.Player;
import org.example.exmod.Constants;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class EncodedPlayerReliantAudioPacket extends ProxPacket {

    byte[] bytes;

    public EncodedPlayerReliantAudioPacket() {}

    public EncodedPlayerReliantAudioPacket(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public void read(IKeylessDeserializer deserializer) throws IOException {
        this.bytes = deserializer.readByteArrayAsNative();
    }

    @Override
    public void write(IKeylessSerializer serializer) throws IOException {
        serializer.writeByteArray(this.bytes);
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
        Player player = GameSingletons.getPlayerFromUniqueId(getOriginPlayerUniqueId());
        if (player != null) Constants.audioPlaybackThread.queue(this.bytes, player.getPosition(), player.getEntity().viewDirection);
    }
}
