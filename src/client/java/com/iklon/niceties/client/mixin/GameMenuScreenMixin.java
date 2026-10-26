package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.NiceScreen;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    //@Shadow
    //protected abstract ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier);

    protected GameMenuScreenMixin(Component title) {
        super(title);
    }
    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;I)Lnet/minecraft/client/gui/layouts/LayoutElement;"))
    void init(CallbackInfo ci, @Local(name = "helper") GridLayout.RowHelper helper) {
        System.out.println("pause menu inject");
//        adder.add(ButtonWidget.builder(Text.of("text"), button -> {
//            button.active = false;
//            assert this.client != null;
//            this.client.setScreen(new VioScreen("config"));
//        }).build());
        helper.addChild(Button.builder(Component.literal("Niceties"), (button) -> { this.minecraft.gui.setScreen(new NiceScreen(Component.literal("config"))); }).build());
    }
}
