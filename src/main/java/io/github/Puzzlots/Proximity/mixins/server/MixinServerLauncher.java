package io.github.Puzzlots.Proximity.mixins.server;

import finalforeach.cosmicreach.server.ServerLauncher;
import finalforeach.cosmicreach.settings.ServerSettings;
import io.github.Puzzlots.Proximity.threading.ThreadBuilder;
import io.github.Puzzlots.Proximity.io.networking.Server;
import io.github.Puzzlots.Proximity.io.networking.packets.ProxPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLauncher.class)
public class MixinServerLauncher {

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/networking/netty/NettyServer;run()V", shift = At.Shift.BEFORE))
    private static void main(String[] args, CallbackInfo ci) {
        ProxPacket.register();

        int port = ServerSettings.SERVER_PORT.getValue();
        ThreadBuilder builder = ThreadBuilder.create("UDP-SERVER-THREAD", () -> {
            try {
                Server.start(port);
                System.out.println("UDP Voice-Chat Server started on port " + port);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        builder.setThreadDaemonState(true);
        builder.finish().start();
    }

}
