package com.iklon.niceties.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    @Accessor("lastSentInput")
    public void niceties$setLastSentInput(Input input);
    @Invoker("modifyInput")
    public Vec2 niceties$modifyInput(final Vec2 input);
}
