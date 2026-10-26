package com.iklon.niceties.client.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory inventory, Component title) {
        super(menu, recipeBookComponent, inventory, title);
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    void containerTick(CallbackInfo ci) {
//        var thePlayer = minecraft.player;
//        assert thePlayer != null;
//        var inputVec = thePlayer.input.getMoveVector();
//        float sin = Mth.sin(thePlayer.getYRot() * (float) (Math.PI / 180.0));
//        float cos = Mth.cos(thePlayer.getYRot() * (float) (Math.PI / 180.0));
//        var finalVec = new Vec3(inputVec.x * cos - inputVec.y * sin,
//                thePlayer.isJumping() ? 1 : 0,
//                inputVec.y * cos + inputVec.x * sin);
//        thePlayer.setDeltaMovement(finalVec.add(thePlayer.isShiftKeyDown() ? -1 : 0).multiply(new Vec3((double) 1 /3, 1, (double) 1 /3)));
    }

}
