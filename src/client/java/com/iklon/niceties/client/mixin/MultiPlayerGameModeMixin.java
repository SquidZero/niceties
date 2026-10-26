package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    protected abstract void startPrediction(ClientLevel level, PredictiveAction predictiveAction);
    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    public abstract boolean destroyBlock(BlockPos pos);

    @Inject(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"))
    void startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (NicetiesClient.instance.fastMine) {
            for (int i = 0; i < NicetiesClient.instance.numAtks; i++) {
                this.startPrediction(this.minecraft.level, sequence -> {
                    this.destroyBlock(pos);
                    return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
                });
            }
            this.startPrediction(this.minecraft.level, sequence -> {
                this.destroyBlock(pos);
                return new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, direction, sequence);
            });
        }
    }

    @ModifyArg(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"), index = 1)
    PredictiveAction placeBeneath(PredictiveAction predictiveAction, @Local(argsOnly = true, name = "hand") InteractionHand hand, @Local(name = "result") MutableObject<InteractionResult> result) {
        if (minecraft.player != null && NicetiesClient.instance.placingBeneath) {
            result.setValue(InteractionResult.PASS);
            NicetiesClient.instance.placingBeneath = false;
            return (sequence) -> new ServerboundUseItemOnPacket(hand, new BlockHitResult(minecraft.player.position().subtract(new Vec3(0, 1, 0)), Direction.DOWN, minecraft.player.blockPosition().below(), true), sequence);
        } else return predictiveAction;
    }

    @WrapMethod(method = "hasMissTime")
    boolean hasMissTime(Operation<Boolean> original) {
        if (NicetiesClient.instance.autoClick) {
            return false;
        } else {
            return original.call();
        }
    }


}
