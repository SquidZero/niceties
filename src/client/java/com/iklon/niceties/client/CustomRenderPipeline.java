package com.iklon.niceties.client;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.iklon.niceties.Niceties;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

/*
public class CustomRenderPipeline implements ClientModInitializer {
    // #region custom_pipelines_define_pipeline
    private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Niceties.MOD_ID, "pipeline/debug_filled_box_through_walls"))
            .withDepthStencilState(Optional.empty())
            .build()
    );
    // #endregion custom_pipelines_define_pipeline
    // #region custom_pipelines_extraction_phase
    private WaypointRenderState waypointState;

    // #endregion custom_pipelines_extraction_phase
    // #region custom_pipelines_drawing_phase
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Waypoints Buffer", RenderType.SMALL_BUFFER_SIZE);

    // #endregion custom_pipelines_drawing_phase

    @Override
    public void onInitializeClient() {
        LevelExtractionEvents.END_EXTRACTION.register(this::extractWaypoint);
        LevelRenderEvents.END_MAIN.register(this::renderAndDrawWaypoint);
        NicetiesClient.pipeline = this;
    }

    // #region custom_pipelines_extraction_phase
    private void extractWaypoint(LevelExtractionContext context) {
        // Access data from the world or anything here in the extraction phase.
        // You can only access the (immutable and thread safe) render state in the drawing phase.
        this.waypointState = new WaypointRenderState(0, 100, 0, 0f, 1f, 0f, 0.5f);
    }

    // #endregion custom_pipelines_extraction_phase
    // #region custom_pipelines_drawing_phase
    private void renderAndDrawWaypoint(LevelRenderContext context) {
        RenderPipeline renderPipeline = CustomRenderPipeline.FILLED_THROUGH_WALLS;
        VertexFormat formatBinding = renderPipeline.getVertexFormatBinding(0);

        assert formatBinding != null;

        PrimitiveTopology primitive = renderPipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(formatBinding, primitive, primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        this.renderWaypoint(context, draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if (info != null) {
            draw(Minecraft.getInstance(), info, renderPipeline);
        }

        stagedBuffer.endFrame();
    }

    private void renderWaypoint(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        final VertexConsumer builder = stagedBuffer.getVertexBuilder(draw);

        this.renderFilledBox(matrices.last().pose(), builder, this.waypointState.x(), this.waypointState.y(), this.waypointState.z(), this.waypointState.x() + 1, this.waypointState.y() + 1, this.waypointState.z() + 1, this.waypointState.r(), this.waypointState.g(), this.waypointState.b(), this.waypointState.a());

        matrices.popPose();
    }

    private void renderFilledBox(Matrix4fc positionMatrix, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        // Front Face
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Back face
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // Left face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Right face
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Top face
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Bottom face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }

    private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> Niceties.MOD_ID + " example render pipeline rendering", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {

            renderPass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            // Bind texture if applicable:
            // Sampler0 is used for texture inputs in vertices
            // renderPass.bindTexture("Sampler0", textureSetup.texure0(), textureSetup.sampler0());

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

            // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }
    // #endregion custom_pipelines_drawing_phase

    // #region custom_pipelines_clean_up
    public static void close() {
        stagedBuffer.close();
    }
    // #endregion custom_pipelines_clean_up

    // #region custom_pipelines_extraction_phase
    // Render states should be immutable, thread safe, and fast to create.
    private record WaypointRenderState(int x, int y, int z, float r, float g, float b, float a) { }
    // #endregion custom_pipelines_extraction_phase
    public void addPos(BlockPos pos, float red, float green, float blue, float alpha) {
        blocks.put(pos, new Color(red, green, blue, alpha));
    }

    public void removePos(BlockPos pos) {
        blocks.remove(pos);
    }
}*/


public class CustomRenderPipeline implements ClientModInitializer {
    public record Color (
            float r,
            float g,
            float b,
            float a
    ){
        public static final Codec<Color> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, 1).fieldOf("r").forGetter(Color::r),
                Codec.floatRange(0, 1).fieldOf("g").forGetter(Color::g),
                Codec.floatRange(0, 1).fieldOf("b").forGetter(Color::b),
                Codec.floatRange(0, 1).fieldOf("a").forGetter(Color::a)
        ).apply(instance, Color::new));
    }
    public final ConcurrentHashMap<BlockPos, Color> blocks = new ConcurrentHashMap<>();
    @Override
    public void onInitializeClient() {
        LevelExtractionEvents.END_EXTRACTION.register(this::extractWaypoint);
        LevelRenderEvents.END_MAIN.register(this::renderAndDrawWaypoint);
        Niceties.LOGGER.info("constructing render pipeline...");
        NicetiesClient.pipeline = this;
    }


    // :::custom-pipelines:define-pipeline
    private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Niceties.MOD_ID, "pipeline/debug_filled_box_through_walls"))
            .withDepthStencilState(Optional.empty())
            .build()
    );

    private WaypointRenderState waypointState;

    // :::custom-pipelines:define-pipeline
    // :::custom-pipelines:extraction-phase
    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private BufferBuilder buffer;

    // :::custom-pipelines:extraction-phase
    // :::custom-pipelines:drawing-phase
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Waypoints Buffer", RenderType.SMALL_BUFFER_SIZE);

    // :::custom-pipelines:drawing-phase

    // #region custom_pipelines_extraction_phase
    public void extractWaypoint(LevelExtractionContext context) {
        // Access data from the world or anything here in the extraction phase.
        // You can only access the (immutable and thread safe) render state in the drawing phase.
        this.waypointState = new WaypointRenderState(0, 100, 0, new Color(0f, 1f, 0f, 0.5f));
    }

    public void renderAndDrawWaypoint(LevelRenderContext context) {

        RenderPipeline renderPipeline = CustomRenderPipeline.FILLED_THROUGH_WALLS;
        VertexFormat formatBinding = renderPipeline.getVertexFormatBinding(0);

        assert formatBinding != null;

        PrimitiveTopology primitive = renderPipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(formatBinding, primitive, primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        this.renderWaypoint(context, draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if (info != null) {
            draw(Minecraft.getInstance(), info, renderPipeline);
        }

        stagedBuffer.endFrame();
    }

    // :::custom-pipelines:extraction-phase
    private void renderWaypoint(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        Queue<BlockPos> rmQueue = new PriorityQueue<>();

        final VertexConsumer builder = stagedBuffer.getVertexBuilder(draw);

        if (!blocks.isEmpty()) {
            for (Map.Entry<BlockPos, Color> block : blocks.entrySet()) {
                BlockPos pos = block.getKey();
                Color color = block.getValue();
                assert Minecraft.getInstance().level != null;
                if (Minecraft.getInstance().level.getBlockEntity(pos) == null && !NicetiesClient.instance.espBlocks.containsKey(Minecraft.getInstance().level.getBlockState(pos).getBlock()) || !NicetiesClient.instance.containerESP)
                {
                    rmQueue.add(pos);
                } else {
                    AABB box = new AABB(pos);
                    this.renderFilledBox(matrices.last().pose(), builder, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ, color.r, color.g, color.b, color.a);

                }
            }
            for (var pos : rmQueue) {
                blocks.remove(pos);
            }
        }


        this.renderFilledBox(matrices.last().pose(), builder, 0f, 102f, 0f, 1f, 101f, 1f, 0f, 1f, 0f, 0.0f);


        matrices.popPose();
    }

    private void renderFilledBox(Matrix4fc positionMatrix, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        // Front Face
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Back face
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // Left face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Right face
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Top face
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Bottom face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }
    // :::custom-pipelines:extraction-phase

    private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> Niceties.MOD_ID + " example render pipeline rendering", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            // Bind texture if applicable:
            // Sampler0 is used for texture inputs in vertices
            // renderPass.bindTexture("Sampler0", textureSetup.texure0(), textureSetup.sampler0());

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

            // The base vertex is the starting index when we copied the data into the vertex buffer divided by vertex size
            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }

    // :::custom-pipelines:drawing-phase

    // :::custom-pipelines:clean-up
    public void close() {
        allocator.close();
        stagedBuffer.close();
    }
    // :::custom-pipelines:clean-up

    public void addPos(BlockPos pos, float red, float green, float blue, float alpha) {
        blocks.put(pos, new Color(red, green, blue, alpha));
    }

    public void removePos(BlockPos pos) {
        blocks.remove(pos);
    }

    private record WaypointRenderState(int x, int y, int z, Color color) { }

}