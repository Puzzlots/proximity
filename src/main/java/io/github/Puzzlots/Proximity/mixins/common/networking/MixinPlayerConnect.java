package io.github.Puzzlots.Proximity.mixins.common.networking;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.packets.entities.PlayerPacket;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerPacket.class)
public class MixinPlayerConnect {

    @Shadow public Player player;

    @Inject(method = "<init>(Lfinalforeach/cosmicreach/accounts/Account;Lfinalforeach/cosmicreach/entities/player/Player;Z)V", at = @At("TAIL"))
    private void write(CallbackInfo ci) {
//        System.err.println(ServerSingletons.getConnection(player).ctx.channel().remoteAddress());
//        System.err.println(ServerSingletons.getConnection(player).ctx.channel().localAddress());
    }

}
