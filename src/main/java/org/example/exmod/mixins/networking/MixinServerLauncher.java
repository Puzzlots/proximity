package org.example.exmod.mixins.networking;

import finalforeach.cosmicreach.server.ServerLauncher;
import finalforeach.cosmicreach.settings.ServerSettings;
import org.example.exmod.io.networking.Server;
import org.example.exmod.io.networking.packets.ProxPacket;
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
        Thread thread = new Thread(() -> {
            try {
                Server.start(port);
                System.out.println("Server started on port " + port);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Server-Thread");
        thread.setDaemon(true);
        thread.start();
    }

}
