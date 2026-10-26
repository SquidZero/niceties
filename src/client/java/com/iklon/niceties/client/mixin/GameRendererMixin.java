package com.iklon.niceties.client.mixin;

import com.iklon.niceties.client.CustomRenderPipeline;
import com.iklon.niceties.client.NicetiesClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "close", at = @At("RETURN"))
    void onGameRendererClose(CallbackInfo ci) {
        NicetiesClient.pipeline.close();
    }
}
