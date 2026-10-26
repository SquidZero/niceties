package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Predicate;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Inject(method = "submitBlockEntities", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
    void BlockEntityESP(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo ci, @Local(name = "blockPos") BlockPos blockPos) {
        if (NicetiesClient.instance.containerESP) {
            assert Minecraft.getInstance().level != null;
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockPos);
            if (blockEntity != null) {
            BlockState state = blockEntity.getBlockState();
            final Block block = state.getBlock();
                if (block == Blocks.CHEST) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos, 0f, 1f, 0f, 0.5f);
                } else if (block == Blocks.TRAPPED_CHEST) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos, 1f, 0.5f, 0f, 0.35f);        }
                else if (block == Blocks.ENDER_CHEST) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos,  0.68f, 0f, 1f, 0.5f);
                } else if (block == Blocks.SHULKER_BOX) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos,  0.65f, 0.48f, 0.64f, 0.5f);
                } else if (block == Blocks.BARREL) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos, 0.2f, 0.8f, 0.2f, 0.5f);
                } else if (block == Blocks.SPAWNER) {
                    if (!NicetiesClient.pipeline.blocks.containsKey(blockPos))
                        NicetiesClient.pipeline.addPos(blockPos, 0f, 0.5f, 0.4f, 0.4f);
                }
            }
        }
    }
}
