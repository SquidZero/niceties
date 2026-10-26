package com.iklon.niceties.client.mixin;

import net.minecraft.world.entity.ElytraAnimationState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ElytraAnimationState.class)
public class ElytraAnimationStateMixin {
//    @ModifyVariable(method = "tick()V", at = @At("STORE"), name = "movement")
//    Vec3 resetY(Vec3 movement) {
//        movement.multiply(new Vec3(1,0,1));
//        return movement;
//    }
}
