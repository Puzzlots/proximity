package org.example.exmod.io.networking.packets;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import org.example.exmod.io.networking.IProxNetIdentity;
import org.example.exmod.io.serialization.IKeylessDeserializer;
import org.example.exmod.io.serialization.IKeylessSerializer;
import org.example.exmod.io.serialization.KeylessBinarySerializer;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

    public String getOriginPlayerUniqueId() {
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

    short id = -1;

    public short getId() {
        if (id == -1) id = ProxPacket.REVERSE_PACKET_MAP.get(getClass());
        return id;
    }

    public static byte[] setupToSend(ProxPacket packet) throws IOException {
        IKeylessSerializer serializer = new KeylessBinarySerializer();
        packet.preWrite(serializer);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(stream);
        byte[] bytes = serializer.toCompressedBytes();

        short id = packet.getId();
        dataStream.writeShort(id);

        dataStream.writeInt(bytes.length);
        dataStream.write(bytes);

        dataStream.close();
        byte[] streamByteArray = stream.toByteArray();
        stream.close();

        return streamByteArray;
    }

    public static void register() {
        register(0, MessagePacket.class);
        register(1, EncodedPlayerReliantAudioPacket.class);
    }

}
