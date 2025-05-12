package io.github.Puzzlots.Proximity.io.networking.protocol.any;

import com.github.puzzle.core.Constants;
import com.github.puzzle.core.loader.meta.EnvType;
import finalforeach.cosmicreach.GameSingletons;
import io.github.Puzzlots.Proximity.threading.ThreadBuilder;
import io.github.Puzzlots.Proximity.io.networking.Server;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;
import io.github.Puzzlots.Proximity.io.networking.protocol.udp.UDPProxNetIdentity;
import io.github.Puzzlots.Proximity.io.serialization.KeylessBinaryDeserializer;
import io.github.Puzzlots.Proximity.player.IProxPlayer;
import io.github.Puzzlots.Proximity.threading.Threads;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketHandlingThread implements Runnable {

    public static final PacketHandlingThread INSTANCE;

    static {
        ThreadBuilder builder = ThreadBuilder.create("PACKET-EXECUTION-THREAD", INSTANCE = new PacketHandlingThread());
        builder.setThreadDaemonState(true);
        Threads.PACKET_EXECUTION_THREAD = builder.finish();
    }

    public static void start() {
        Threads.PACKET_EXECUTION_THREAD.start();
    }

    private static final Queue<PacketHandleRequest> packets = new ConcurrentLinkedQueue<>();

    public static void queue(PacketHandleRequest request) {
        PacketHandlingThread.packets.add(request);
    }

    @Override
    public void run() {
        while (true) {
            if (PacketHandlingThread.packets.isEmpty()) continue;

            PacketHandleRequest request = PacketHandlingThread.packets.poll();
            try {
                ProxPacket packet = request.packetClass().getConstructor().newInstance();
                packet.preRead(KeylessBinaryDeserializer.fromBytes(request.data(), true));

                if (Server.useUDP && Constants.SIDE == EnvType.SERVER) {
                    IProxPlayer player;
                    if ((player = (IProxPlayer) GameSingletons.getPlayerFromUniqueId(packet.getOriginPlayerUniqueId())) != null && player.needsIdentity()) {
                        player.setUdpAddress(request.sender());
                        player.setUDPIdentity((UDPProxNetIdentity) request.identity());
                    }
                }

                packet.handle(Constants.SIDE, request.identity());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
