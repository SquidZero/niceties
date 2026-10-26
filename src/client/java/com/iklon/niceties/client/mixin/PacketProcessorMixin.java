package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import net.minecraft.ReportedException;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketProcessor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(PacketProcessor.class)
public abstract class PacketProcessorMixin implements AutoCloseable {
    @Inject(method = "scheduleIfPossible", at = @At(value = "HEAD"))
    <T extends PacketListener> void logPackets(T listener, Packet<T> packet, CallbackInfo ci) {
//        if (packet instanceof ClientboundSetEntityMotionPacket entityMotionPacket)
//            Niceties.LOGGER.info("{} | {}", packet.type().toString(), entityMotionPacket.movement());
        if (NicetiesClient.logPackets) {
            if (!(packet instanceof ServerboundClientTickEndPacket) && !(packet instanceof ClientboundLevelChunkWithLightPacket)
            && !(packet instanceof ClientboundSetTimePacket) && !(packet instanceof ServerboundChunkBatchReceivedPacket)) {
                if (packet instanceof ServerboundMovePlayerPacket movePacket)
                Niceties.LOGGER.info("{} | X: {} Y: {} Z: {}", packet.type(), movePacket.getX(6969), movePacket.getY(6969), movePacket.getZ(6969));
                if (packet instanceof ServerboundUseItemOnPacket itemUsePacket) {
                    Niceties.LOGGER.info("{} | {}", packet.type(), itemUsePacket.getHitResult());
                }
                else Niceties.LOGGER.info("{}", packet.type());
            }
        }
    }
}

