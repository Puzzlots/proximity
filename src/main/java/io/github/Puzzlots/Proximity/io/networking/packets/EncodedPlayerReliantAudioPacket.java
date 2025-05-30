package io.github.Puzzlots.Proximity.io.networking.packets;

import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.player.Player;
import io.github.Puzzlots.Proximity.Constants;
import io.github.Puzzlots.Proximity.io.networking.IProxNetIdentity;
import io.github.Puzzlots.Proximity.io.networking.Server;
import io.github.Puzzlots.Proximity.io.serialization.IKeylessDeserializer;
import io.github.Puzzlots.Proximity.io.serialization.IKeylessSerializer;
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
