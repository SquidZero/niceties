package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    public abstract InputType getLastInputType();

    @Shadow
    private InputType lastInputType;

    @Shadow
    @Final
    public MouseHandler mouseHandler;

    @Shadow
    @Final
    public Options options;

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    private static Minecraft instance;

    @Shadow
    public int missTime;

    public MinecraftMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @WrapMethod(method = "startAttack")
    boolean attack3times(Operation<Boolean> original) {
        for (int i = 0; i < NicetiesClient.instance.numAtks; i++) {
            original.call();
        }
        return false;
    }

    @Inject(method = "close", at = @At("HEAD"))
    void runTick(CallbackInfo ci) {
        NicetiesClient.instance.shutdownESP();
    }



    @Unique
    int i = 0;
    @Unique
    boolean didDown = false;
    @ModifyArg(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
    boolean autoContinue(boolean down) {
        if (NicetiesClient.instance.autoClick) {
            if (i < NicetiesClient.instance.numAtks && NicetiesClient.instance.autoClickSpam) {
                i++;
                //didDown = true;
                return true;
            } else {
                i = 0;
                //didDown = false;
                return false;
            }
        }
        else return down;
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", opcode = Opcodes.PUTFIELD))
    void autoClickOnScreen(Minecraft instance, int value) {
        if (!NicetiesClient.instance.autoClick) this.missTime = value;
    }

//    @Inject(method = "runTick", at = @At("TAIL"))
//    void runtick(boolean advanceGameTime, CallbackInfo ci) {
//    }

}
