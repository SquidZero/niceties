package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements ItemLike, FabricBlock {
    @Shadow
    @Final
    protected StateDefinition<Block, BlockState> stateDefinition;

    public BlockMixin(Properties properties) {
        super(properties);
    }

    @WrapMethod(method = "shouldRenderFace")
    private static boolean ESP(BlockState state, BlockState neighborState, Direction direction, Operation<Boolean> original) {
        if (state.getBlock() == Blocks.CHEST) {
            //NicetiesClient.pipeline.blocks.add(state.getFaceOcclusionShape(direction).bounds());
            return true;
        } else {
            //return false;
            return original.call(state, neighborState, direction);
        }
    }
}
