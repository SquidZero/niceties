package com.iklon.niceties.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public abstract interface MinecraftAccessor {
    @Invoker("startAttack")
    public boolean niceties$startAttack();
}
