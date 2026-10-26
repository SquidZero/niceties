package com.iklon.niceties.client;

import com.iklon.niceties.Niceties;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ESPScannerTask implements Runnable {
    public static boolean scan = false;
    Minecraft mc;
    public static long scanTime;

    Predicate<BlockPos> predicate = (pos) -> {
        assert mc.level != null;
        Block block = mc.level.getBlockState(pos).getBlock();
        if (NicetiesClient.instance.espBlocks.containsKey(block) && !NicetiesClient.pipeline.blocks.containsKey(pos)) {
            //Niceties.LOGGER.info("add block");
            var espDef = NicetiesClient.instance.espBlocks.get(block);
            NicetiesClient.pipeline.addPos(pos, espDef.r(), espDef.g(), espDef.b(), espDef.a());
            return true;
        }
        return false;
    };
    @Override
    public void run() {
        long start = Util.getNanos();
        if (scan) {
            mc = Minecraft.getInstance();
            int r = NicetiesClient.instance.espDistance;
            if (mc.player != null) {
                //BlockPos.findClosestMatch(mc.player.blockPosition(), r, r, predicate);
                var s = StreamSupport.stream(BlockPos.withinManhattan(mc.player.blockPosition(), r, r, r).spliterator(), true).iterator();
                while (s.hasNext()) {
                    if (predicate.test(s.next())) {
                        break;
                    }
                }
            }
            scanTime = Util.getNanos() - start;
            //Niceties.LOGGER.info("block scan took {} nano seconds", System.nanoTime() - start);
            //Niceties.LOGGER.info("{} {} {}", player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());        };
        }
    }
}
