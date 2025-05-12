package io.github.Puzzlots.Proximity.mixins.client;

import finalforeach.cosmicreach.networking.client.netty.NettyClient;
import io.netty.channel.ChannelHandlerContext;
import io.github.Puzzlots.Proximity.io.networking.client.Client;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NettyClient.class)
public class NettyClientMixin {

    @Inject(method = "close()V", at = @At("HEAD"))
    public void close(CallbackInfo ci) {
        Client.shutdown();
    }

    @Inject(method = "close(Ljava/lang/String;)V", at = @At("HEAD"))
    public void close(String reason, CallbackInfo ci) {
        Client.shutdown();
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, CallbackInfo ci) {
        Client.shutdown();
    }

}
