package org.example.exmod.io.networking.packets;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.player.Player;
import org.example.exmod.Constants;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PlayerReliantAudioPacket extends ProxPacket {

    short[] shorts;

    public PlayerReliantAudioPacket() {}

    public PlayerReliantAudioPacket(short[] shorts) {
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
        Player player = GameSingletons.getPlayerFromUniqueId(getPlayerUniqueId());
        if (player != null) Constants.audioPlaybackThread.queue(shorts, player.getPosition());
        else System.out.println(player);
    }
}
