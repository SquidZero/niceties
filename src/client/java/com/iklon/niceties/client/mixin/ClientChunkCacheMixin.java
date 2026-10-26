package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import com.mojang.datafixers.DataFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.GeneratingChunkMap;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin extends ChunkSource {
//    @Shadow
//    @Final
//    private ClientLevel level;
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    void tick(BooleanSupplier haveTime, boolean tickChunks, CallbackInfo ci) {
//        var renderDist = 16; //Minecraft.getInstance().levelRenderer.getLastViewDistance();
//        assert Minecraft.getInstance().player != null;
//        var box = AABB.ofSize(Minecraft.getInstance().player.position(), renderDist, renderDist, renderDist);
//        Iterable<BlockPos.MutableBlockPos> positions = BlockPos.spiralAround(Minecraft.getInstance().player.blockPosition(), 16, Direction.NORTH, Direction.EAST);
//        positions.forEach((blockPos) -> {
//            if (NicetiesClient.espBlocks.containsKey(this.level.getBlockState(blockPos).getBlock())) {
//                if (!NicetiesClient.pipeline.blocks.containsKey(blockPos)) {
//                    //Niceties.LOGGER.info("sugar cane added pos {}", blockPos);
//                    NicetiesClient.pipeline.addPos(blockPos, 0f, 1f, 0.2f, 0.5f);
//                }
//            }
//        });
//    }
}
