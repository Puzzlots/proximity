package org.example.exmod.mixins;

import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import org.example.exmod.io.networking.client.Client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientNetworkManager.class)
public class ClientNetworkManagerMixin {

    @Inject(method = "connectToServer", at = @At("TAIL"))
    private static void connectToServer(String address, Runnable onConnect, Consumer<Throwable> onFailure, CallbackInfo ci) {
        String ip;
        int port;
        if (address.contains(":")) {
            String[] parts = address.split(":");
            ip = parts[0];
            port = Integer.parseInt(parts[1]) + 1;
        } else {
            ip = address;
            port = 47137;
        }

        Thread thread = new Thread(() -> {
            try {
                Client.connect(ip, port);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Client-Thread");
        thread.setDaemon(true);
        thread.start();
    }
}