package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.iklon.niceties.client.NicetiesClient.*;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private boolean lastHorizontalCollision;

    //@Shadow
    //protected abstract boolean wouldCollideAt(BlockPos pos);

    //@Shadow
    //public abstract boolean isInWater();

    @Shadow
    private boolean wasFallFlying;
    @Shadow
    @Final
    public ClientPacketListener connection;
    @Shadow
    public ClientInput input;

    @Shadow
    public abstract boolean isShiftKeyDown();

    @Shadow
    private @Nullable InteractionHand usingItemHand;
    @Shadow
    @Final
    protected Minecraft minecraft;
    @Unique public boolean isFlying = false;
    @Unique int tickCounter = 0;
    @Unique int fallCounter = 0;

    public LocalPlayerMixin(Minecraft minecraft, ClientLevel level, ClientPacketListener connection, StatsCounter stats, ClientRecipeBook recipeBook, Input lastSentInput, boolean wasSprinting, ChatAbilities chatAbilities) {
        super(level, connection.getLocalGameProfile());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void updatekbds(CallbackInfo ci) {
        while (addvelkey.consumeClick()) {
            NicetiesClient.instance.addedVel += 1;
            System.out.println("up" + instance.addedVel);
        }
        while (decvelkey.consumeClick()) {
            instance.addedVel -= 1;
            System.out.println("down" + instance.addedVel);
        }
        while (noSlowKey.consumeClick()) {
            instance.noSlow = !instance.noSlow;
            NicetiesClient.instance.sendChatMsg("noSlow " + instance.noSlow);
        }
    }


    @WrapMethod(method = "modifyInput")
    Vec2 disregardMovementSpeedFactors(Vec2 input, Operation<Vec2> original) {
        if (instance.noSlow) return input; else return original.call(input);
    }
    @Inject(method = "canStartSprinting", at = @At("RETURN"), cancellable = true)
    private void permasprint(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
//    @ModifyArg(method = "sendMovementPackets()V", at = @At(value = "HEAD", target = "Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket$Full;<init>(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;Lnet/minecraft/util/math/Vec3d;FFZZ)V"))
//    public Vec3d sendMovement()
//    {
//        if (doNoClip)
//        {
//            Vec3d pos = this.getEntityPos();
//            getMovementDirection();
//            return new Vec3d(pos.x + getMovementDirection().getOffsetX(),
//                    pos.y + getMovementDirection().getOffsetY(), pos.z + getMovementDirection().getOffsetZ());
//        }
//        else return getEntityPos();
//    }

    @ModifyArg(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"), index = 1)
    public Vec3 move(Vec3 movement) {
        /*
        System.out.println("addedVel: " + addedVel);
        System.out.println("current movement: " + movement);
        System.out.println("newmovement: " + newMovement);
         */
        if (this.input.getMoveVector() == Vec2.ZERO && instance.elytraFly) return new Vec3 (0, movement.y, 0);

        return new Vec3(movement.x * instance.addedVel, movement.y, movement.z * instance.addedVel);
    }
//    @Inject(method = "move", at = @At("TAIL"))
//    void move(MoverType moverType, Vec3 delta, CallbackInfo ci) {
//        if (instance.elytraFly && isFallFlying()) {
//            var inputVec = this.input.getMoveVector();
//            //Niceties.LOGGER.info("input: {}, {}", inputVec.x, inputVec.y);
//            float sin = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
//            float cos = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
//            var finalVec = new Vec3(inputVec.x * cos - inputVec.y * sin,
//                    this.isJumping() ? 1 : 0,
//                    inputVec.y * cos + inputVec.x * sin);
//            this.setDeltaMovement(finalVec.add(this.isShiftKeyDown() ? -1 : 0));
//        }
//    }
    @Inject(method = "sendPosition", at = @At("HEAD"))
    void aiStep(CallbackInfo ci) {
        if (instance.elytraFly && isFallFlying()) {
            var inputVec = this.input.getMoveVector();
            float sin = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            float cos = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            var finalVec = new Vec3(inputVec.x * cos - inputVec.y * sin,
                    this.isJumping() ? 1 : 0,
                    inputVec.y * cos + inputVec.x * sin);
            this.setDeltaMovement(finalVec.add(this.isShiftKeyDown() ? -1 : 0));
        }
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"))
    public void movebetter(CallbackInfo ci) {
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.isAlive()) {
                while (flykey.consumeClick()) {
                    isFlying = !isFlying;
                    instance.flying = !instance.flying;
                    System.out.println("fly mode " + isFlying);
                }
                if (isFlying) {
                        Vec3 v = new Vec3(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                        tickCounter++;
                        if (tickCounter > 45) {
                            v = new Vec3(v.x, v.y - 0.04, v.z);
                            tickCounter = 0;
                        } else if (tickCounter == 2) {
                            v = new Vec3(v.x, v.y + 0.04, v.z);
                        }
                        if (Minecraft.getInstance().options.keyJump.isDown()) {
                            v = new Vec3(v.x, v.y + 0.5 * (instance.addedVel / 2), v.z);
                        } else if (Minecraft.getInstance().options.keyShift.isDown()) {
                            v = new Vec3(v.x, v.y - 0.5 * (instance.addedVel / 2), v.z);
                        }
                        player.setDeltaMovement(v);
                }
            }
        }
        ));
    }

    @WrapMethod(method = "drop")
    boolean confirmDrop(boolean all, Operation<Boolean> original) {
        var item = this.getInventory().getSelectedItem();
        if (item.isEnchanted() || item.getRarity().compareTo(Rarity.RARE) >= 0) {
            minecraft.gui.setOverlayMessage(Component.literal("are you sure you want to drop that?").withStyle(ChatFormatting.RED), true);
            return false;
        } else {
            return original.call(all);
        }
    }

    @WrapMethod(method = "isCrouching")
    boolean isCrouching(Operation<Boolean> original) {
        if (instance.elytraFly) return false;
        else return original.call();
    }

//    @Inject(method = "moveTowardsClosestSpace", at = @At("RETURN"))
//    public void stopFall(CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) double x, @Local(ordinal = 1, argsOnly = true) double z)
//    {
//        //BlockPos blockPos = BlockPos.ofFloored(x, this.getY(), z);
//        if (wouldFall(this.blockPosition().below()) && !isStayingOnGroundSurface() && !isInWater() && fallCounter <= 10) {
//            //System.out.println("would collide");
//            //super.setVelocity(this.getVelocity().x, this.getVelocity().y/4, this.getVelocity().z);
//            //fallCounter++;
//        } else fallCounter = 0;
//    }
//
//    @Unique
//    boolean wouldFall(BlockPos pos) {
//        AABB box = this.getBoundingBox();
//        if (Math.abs(getDeltaMovement().y) < 0.01) {
//            setOnGround(true);
//            stopFallFlying();// = false;
//            //System.out.println(getVelocity().y);
//            return false;
//        }
//        AABB box2 = new AABB(pos.getX(), box.minY+getDeltaMovement().y, pos.getZ(), pos.getX() + 1.0, box.maxY, pos.getZ() + 1.0).contract(1.0E-7, 0, 0);
//        return this.getEntityWorld().canCollide(this, box2);
//    }



}