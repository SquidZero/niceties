package com.iklon.niceties.client.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public abstract interface ClientInputAccessor {
    @Accessor("moveVector")
    public void niceties$setMoveVector(Vec2 vec);
}
