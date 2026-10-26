package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerboundPlayerActionPacket.class)
public abstract class ServerBoundPlayerActionPacketMixin implements Packet<ServerGamePacketListener> {
    @ModifyVariable(method = "<init>(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket$Action;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;I)V", at = @At("HEAD"), order = 1, argsOnly = true, name = "action")
    private static ServerboundPlayerActionPacket.Action forceBlockBreak(ServerboundPlayerActionPacket.Action action) {
        if (action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            Niceties.LOGGER.info("packet is start_destroy_block");
            return ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;
        } else {
            return action;
        }
    }
}
