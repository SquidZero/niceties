package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.ESPScannerTask;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeltaTracker.Timer.class)
public abstract class TimerMixin implements DeltaTracker {
    @Inject(method = "updatePauseState", at = @At("HEAD"))
    void updateESPScan(boolean pauseState, CallbackInfo ci) {
        ESPScannerTask.scan = !pauseState;
    }
}
