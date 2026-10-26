package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected Vec3 stuckSpeedMultiplier;

    @WrapMethod(method = "makeStuckInBlock")
    void speedMultiplier(BlockState blockState, Vec3 speedMultiplier, Operation<Void> original) {
        if ((Entity)(Object)(this) instanceof LocalPlayer && NicetiesClient.instance.noSlow) {
            this.stuckSpeedMultiplier = Vec3.ZERO;
            //Niceties.LOGGER.info("web");
        } else original.call(blockState, speedMultiplier);
    }
}
