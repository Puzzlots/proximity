package org.example.exmod.io.networking.packets;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.networking.tcp.TCPProxNetIdentity;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ProxPacket {

    public static final Map<Short, Class<? extends ProxPacket>> PACKET_MAP = new HashMap<>();
    public static final Map<Class<? extends ProxPacket>, Short> REVERSE_PACKET_MAP = new HashMap<>();

    private String playerUniqueId;

    public final void preRead(IKeylessDeserializer deserializer) throws IOException {
        playerUniqueId = deserializer.readString();

        this.read(deserializer);
    }

    public final void preWrite(IKeylessSerializer serializer) throws IOException {
        if (Constants.SIDE == EnvType.CLIENT) {
            serializer.writeString(GameSingletons.client().getAccount().getUniqueId());
        } else {
            serializer.writeString(playerUniqueId);
        }

        this.write(serializer);
    }

    public String getPlayerUniqueId() {
        return playerUniqueId;
    }

    abstract public void read(IKeylessDeserializer deserializer) throws IOException;
    abstract public void write(IKeylessSerializer serializer) throws IOException;

    /**
     * @param type the Enviornment Client/Server
     * @param proxNetIdentity WILL be null on client, use Client.context or Client.send
     */
    abstract public void handle(EnvType type, @Nullable IProxNetIdentity proxNetIdentity);

    static void register(int id, Class<? extends ProxPacket> packetClass) {
        PACKET_MAP.put((short) id, packetClass);
        REVERSE_PACKET_MAP.put(packetClass, (short) id);
    }

    public static void register() {
        register(0, MessagePacket.class);
        register(1, NonLocationalAudioPacket.class);
        register(2, LocationalAudioPacket.class);
        register(3, PlayerReliantAudioPacket.class);
    }

}
