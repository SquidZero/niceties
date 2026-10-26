package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract void travel(Vec3 input);

    @Shadow
    public abstract float getSpeed();

    @Shadow
    public abstract void jumpFromGround();

    @Shadow
    protected abstract void travelInAir(Vec3 input);

    @Shadow
    public abstract boolean isJumping();

    @Shadow
    public abstract boolean onClimbable();

    @Shadow
    protected abstract double getEffectiveGravity();

    @Shadow
    protected abstract double getDefaultGravity();

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @WrapMethod(method = "isCurrentlyGlowing")
    public boolean playerGlow(Operation<Boolean> original) {
        if (NicetiesClient.instance.playerGlow == true && ((LivingEntity)(Object)(this) instanceof Player)) {
            return true;
        }
        else {
            return original.call();
        }
    }

//    @Inject(method = "knockDownwards", at = @At("HEAD"), cancellable = true)
//    void disableKnowndown(CallbackInfo ci) {
//        if ((LivingEntity)(Object)(this) instanceof ClientPlayerEntity) ci.cancel();
//    }
//    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
//    void nullKb(double strength, double x, double z, CallbackInfo ci) {
//        if ((LivingEntity)(Object)(this) instanceof ClientPlayerEntity) ci.cancel();
//    }
    //    @Inject(method = "getBaseWaterMovementSpeedMultiplier", at = @At("HEAD"), cancellable = true)
//    void fastWater(CallbackInfoReturnable<Float> cir) {
//        if ((LivingEntity)(Object)(this) instanceof ClientPlayerEntity) cir.setReturnValue(1.F);
//    }
//    @WrapMethod(method = "travelFlying(Lnet/minecraft/util/math/Vec3d;FFF)V")
//    void fixFluidMovement(Vec3d movementInput, float inWaterSpeed, float inLavaSpeed, float regularSpeed, Operation<Void> original) {
//        if ((LivingEntity)(Object)(this) instanceof ClientPlayerEntity) original.call(movementInput, regularSpeed, regularSpeed, regularSpeed);
//    }

    @WrapMethod(method = "handleRelativeFrictionAndCalculateMovement")
    Vec3 longName(Vec3 input, float friction, Operation<Vec3> original) {
        var or = original.call(input, friction);
        if (((LivingEntity)(Object)(this) instanceof LocalPlayer player)) {
            var delta = this.getDeltaMovement();
            if (this.onClimbable()) {
                boolean jumping = player.input.keyPresses.jump();
                if (jumping) {
                    return new Vec3(delta.x, 0.5, delta.z);
                } else return delta;
            }
        }
        return or;
    }

    @WrapMethod(method = "handleOnClimbable")
    Vec3 handleOnClimbable(Vec3 delta, Operation<Vec3> original) {
        return delta;
    }

    @WrapMethod(method = "updateFallFlyingMovement")
    Vec3 elytraFly(Vec3 movement, Operation<Vec3> original) {
        if ((LivingEntity) (Object) (this) instanceof LocalPlayer thePlayer && NicetiesClient.instance.elytraFly) {
            var inputVec = thePlayer.input.getMoveVector();
            float sin = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            float cos = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            var finalVec = new Vec3(inputVec.x * cos - inputVec.y * sin,
                    thePlayer.isJumping() ? 1 : 0,
                    inputVec.y * cos + inputVec.x * sin);
            return finalVec.add(thePlayer.isShiftKeyDown() ? -1 : 0).multiply(new Vec3((double) 1 /3, 1, (double) 1 /3));
        }
         else {
            return original.call(movement);
        }
    }

    @WrapMethod(method = "travelInFluid")
    void fixFluidMovement2(Vec3 input, Operation<Void> original) {
        if ((LivingEntity)(Object)(this) instanceof LocalPlayer && !NicetiesClient.instance.waterPhysics) this.travelInAir(input); else original.call(input);
    }

    @WrapMethod(method = "jumpInLiquid")
    void swimUpward(TagKey<@NotNull Fluid> type, Operation<Void> original) {
        if ((LivingEntity)(Object)(this) instanceof LocalPlayer && !NicetiesClient.instance.waterPhysics) this.jumpFromGround(); else original.call(type);
    }

    @WrapMethod(method = "drop")
    ItemEntity confirmDrop(ItemStack itemStack, boolean thrownFromHand, Prediction prediction, Operation<ItemEntity> original) {
        if ((LivingEntity)(Object)(this) instanceof LocalPlayer player && NicetiesClient.instance.preventDrop) {
            Minecraft minecraft = Minecraft.getInstance();
            var item = player.getInventory().getSelectedItem();
            if (item.isEnchanted() || item.getRarity().compareTo(Rarity.RARE) >= 0) {
                minecraft.gui.hud.setOverlayMessage(Component.literal("are you sure you want to drop that?").withStyle(ChatFormatting.RED), true);
                return null;
            } else {
                return original.call(itemStack, thrownFromHand, prediction);
            }
        } else {
            return original.call(itemStack, thrownFromHand, prediction);
        }
    }

//    @WrapMethod(method = "shouldSwimInFluids")
//    boolean shouldntSwimInFluids(Operation<Boolean> original) {
//        return false;
//    }

}

