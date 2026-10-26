package com.iklon.niceties.client.mixin;

import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.NicetiesClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChestSpecialRenderer.class)
public abstract class ChestSpecialRendererMixin implements NoDataSpecialModelRenderer {
    @Shadow
    @Final
    private ChestModel model;

    @Shadow
    @Final
    private float openness;

    @Shadow
    @Final
    private SpriteId sprite;

    @Shadow
    @Final
    private SpriteGetter sprites;

    @WrapMethod(method = "submit")
    void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor, Operation<Void> original){
        //Niceties.LOGGER.info("submit render");

        submitNodeCollector.submitModel(this.model, this.openness, poseStack, lightCoords, overlayCoords, 0xff1cdeff, this.sprite, this.sprites, 0xaaffffff);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.secondaryBlockOutline(), (pose, builder) -> renderCustomGeometry(pose, builder));
    }

    @Unique
    void renderCustomGeometry(PoseStack.Pose pose, VertexConsumer builder) {
        List<ModelPart> modelParts = model.allParts();
        for (var modelPart : modelParts) {
            builder.addVertex(pose, modelPart.x, modelPart.y, modelPart.z).setColor(0xffffff).setLineWidth(1.0F).setNormal(0F, 1F, 0F);
        }
    }
}
