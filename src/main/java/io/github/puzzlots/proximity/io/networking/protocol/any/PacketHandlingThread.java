package io.github.puzzlots.proximity.io.networking.protocol.any;

import dev.puzzleshq.puzzleloader.loader.util.EnvType;
import finalforeach.cosmicreach.accounts.Account;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import io.github.puzzlots.proximity.threading.ThreadBuilder;
import io.github.puzzlots.proximity.io.networking.Server;
import io.github.puzzlots.proximity.io.networking.packets.ProxPacket;
import io.github.puzzlots.proximity.io.serialization.KeylessBinaryDeserializer;
import io.github.puzzlots.proximity.player.IProxPlayer;
import io.github.puzzlots.proximity.threading.Threads;

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

                if (Server.useUDP && GameSingletons.isHost) {
                    IProxPlayer player;

                    String uniqueId = packet.getOriginPlayerUniqueId();
                    Account account = ServerSingletons.getAccountByUniqueId(uniqueId);
                    if ((player = (IProxPlayer) account) != null && player.needsIdentity()) {
                        player.setUdpAddress(request.sender());
                        player.setUDPIdentity(request.identity());
                    }
                }

                packet.handle(GameSingletons.isHost ? EnvType.SERVER : EnvType.CLIENT, request.identity());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
