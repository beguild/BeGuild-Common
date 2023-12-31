package dev.frydae.beguild.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.frydae.beguild.BeGuildCommon;
import dev.frydae.beguild.events.ServerPlayerConnectionEvents;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private List<ServerPlayerEntity> players;

    @WrapWithCondition(
            method = "onPlayerConnect",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    public boolean sendToAllRedirect(PlayerManager instance, Packet<?> packet) {
        // This method replaces the one that sends player list entries to everyone

        if (packet instanceof PlayerListS2CPacket newPacket) {
            UUID uuid = newPacket.getEntries().get(0).profileId();
            ServerPlayerEntity playerToSend = BeGuildCommon.getServer().getPlayerManager().getPlayer(uuid);

            List<ServerPlayerEntity> playersToSendTo = ServerPlayerConnectionEvents.SEND_PLAYER_LIST_FILTER.getInvoker().onSendPlayerListFilter(newPacket, playerToSend, players);

            playersToSendTo.forEach(p -> p.networkHandler.sendPacket(newPacket));

            return false;
        }

        return true;
    }
}
