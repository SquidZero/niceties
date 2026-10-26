package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NicetiesClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;releaseAll()V"))
    void setScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.mouseHandler.releaseMouse();
        InputConstants.releaseMouse(Minecraft.getInstance().getWindow(), (int)mc.mouseHandler.xpos(), (int)mc.mouseHandler.ypos());
        if (!NicetiesClient.instance.invMove) {
            KeyMapping.releaseAll();
        } else {
            var useKey = KeyMapping.get(mc.options.keyUse.getName());
            var attackKey = KeyMapping.get(mc.options.keyAttack.getName());
            if (useKey != null) useKey.setDown(false);
            if (attackKey != null) attackKey.setDown(false);
        }
    }}
