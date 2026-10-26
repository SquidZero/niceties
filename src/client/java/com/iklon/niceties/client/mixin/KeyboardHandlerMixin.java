package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(method = "keyPress", at = @At("STORE"), name = "handlesGameInput")
    boolean handleGameInput(boolean handlesGameInput) {
        if (minecraft.screen instanceof ChatScreen) return handlesGameInput;
        else return NicetiesClient.instance.invMove || handlesGameInput;
    }

//    @Inject(method = "keyPress", at = @At("RETURN"))
//    void keyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
//        InputConstants.Key key = InputConstants.getKey(event);
//        if (action == 1) {
//            KeyMapping.set(key, true);
//        } else if (action == 0) {
//            KeyMapping.set(key, false);
//        }
//
//    }
}
