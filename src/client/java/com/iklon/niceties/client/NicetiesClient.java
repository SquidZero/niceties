package com.iklon.niceties.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iklon.niceties.Niceties;
import com.iklon.niceties.client.mixin.MinecraftAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

public class NicetiesClient implements ClientModInitializer {
    public static NicetiesClient instance;
    public static KeyMapping flykey;
    public static KeyMapping addvelkey;
    public static KeyMapping decvelkey;
    public static KeyMapping nukeKey;
    public static KeyMapping noSlowKey;
    public static KeyMapping placeBeneathKey;
    public static KeyMapping playerGlowKey;
    public static KeyMapping nicetiesKey;
    public static KeyMapping autoClickKey;
    public double addedVel = 0;
    public boolean nuke = false;
    public Block maceBlock = null;
    public boolean waterPhysics = true;
    public boolean noSlow = false;
    public boolean flying = false;
    public boolean playerGlow = false;
    public int numAtks = 1;
    public boolean containerESP = false;
    public long espInterval = 200;
    public Map<Block, CustomRenderPipeline.Color> espBlocks = new HashMap<>();
    public int espDistance = 1;
    public boolean scanning;
    public boolean elytraFly = false;
    public boolean fastMine = false;
    public boolean invMove = false;
    public boolean placingBeneath = false;
    public boolean preventDrop = true;
    public boolean autoClick = false;
    public boolean autoClickSpam = false;
    public static final boolean logPackets = false;
    public static CustomRenderPipeline pipeline = null;
    public ClientInput input;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("niceties", "keymappings"));
    public static final Codec<Map<Identifier, CustomRenderPipeline.Color>> ESP_CODEC = Codec.unboundedMap(Identifier.CODEC, CustomRenderPipeline.Color.CODEC);

    static Queue<String> msgq = new PriorityQueue<>();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> espHandle;
    @Override
    public void onInitializeClient() {

        instance = this;
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        flykey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.togglefly",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_K,
                CATEGORY
        ));
        addvelkey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.addvel",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_UP,
                CATEGORY
        ));
        decvelkey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.decreasevel",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_DOWN,
                CATEGORY
        ));
        nukeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.toggleNuke",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_HOME,
                CATEGORY
        ));
        noSlowKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.noSlow",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_BACKSLASH,
                CATEGORY
        ));
        placeBeneathKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.placeBeneath",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_PAGEDOWN,
                CATEGORY
        ));
        playerGlowKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.playerGlow",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_SEMICOLON,
                CATEGORY
        ));
        nicetiesKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.open_niceties",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_RSHIFT,
                CATEGORY
        ));
        autoClickKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.iklon.niceties.autoclick",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_LBRACKET,
                CATEGORY
        ));

//        try (FileReader reader = new FileReader(Minecraft.getInstance().gameDirectory + "/config/niceties_esp.json")) {
//            JsonElement json = JsonParser.parseReader(reader);
//            DataResult<Map<Identifier, CustomRenderPipeline.Color>> deserializeResult = ESP_CODEC.parse(JsonOps.INSTANCE, json);
//            var toConvert = deserializeResult.getOrThrow();
//            assert Minecraft.getInstance().level != null;
//            var reg = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BLOCK);
//            for (var entry : toConvert.entrySet()) {
//                espBlocks.put(reg.get(entry.getKey()).orElseThrow().value(), entry.getValue());
//            }
//        } catch (IOException e) {
//            Niceties.LOGGER.error("error reading esp blocks file: {}", e.getMessage());
//        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("esp")
                    .then(ClientCommands.literal("add")
                            .then(ClientCommands.argument("block", BlockStateArgument.block(registryAccess))
                                    .then(ClientCommands.argument("r", FloatArgumentType.floatArg(0, 1))
                                            .then(ClientCommands.argument("g", FloatArgumentType.floatArg(0, 1))
                                                    .then(ClientCommands.argument("b", FloatArgumentType.floatArg(0, 1))
                                                            .then(ClientCommands.argument("a", FloatArgumentType.floatArg(0, 1))
                                                                    .executes(context -> {
                                                                        var block = context.getArgument("block", BlockInput.class);
                                                                        float r = context.getArgument("r", Float.class);
                                                                        float g = context.getArgument("g", Float.class);
                                                                        float b = context.getArgument("b", Float.class);
                                                                        float a = context.getArgument("a", Float.class);

                                                                        instance.espBlocks.put(block.getState().getBlock(), new CustomRenderPipeline.Color(r, g, b, a));
                                                                        saveESPBlocks();
                                                                        context.getSource().sendFeedback(Component.literal("Added " + block.getState().getBlock().getName() + "to ESP blocks"));
                                                                        //context.getSource().sendFeedback(Component.literal("Current esp: " + espBlocks.keySet().stream().map(Block::getName)));
                                                                        return 1;
                                                                    }))
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(ClientCommands.literal("remove")
                            .then(ClientCommands.argument("block", BlockStateArgument.block(registryAccess))
                                    .executes(context -> {
                                        var block = context.getArgument("block", BlockInput.class);
                                        instance.espBlocks.remove(block.getState().getBlock());
                                        saveESPBlocks();
                                        context.getSource().sendFeedback(Component.literal("removed " + block.getState().getBlock().getName() + "from ESP blocks"));
                                        return 1;
                                    })
                            )

                    )
            );
        });

        instance.input = new KeyboardInput(Minecraft.getInstance().options);

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(Niceties.MOD_ID, "last_element"), ESPHud::extract);

        espHandle = scheduler.scheduleAtFixedRate(new ESPScannerTask(), 0, this.espInterval, TimeUnit.MILLISECONDS);

        var minecraft = Minecraft.getInstance();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            while (NicetiesClient.placeBeneathKey.consumeClick()) {
                if (minecraft.player != null && minecraft.gameMode != null) {
                    NicetiesClient.instance.placingBeneath = true;
                    minecraft.gameMode.useItemOn(minecraft.player, minecraft.player.getUsedItemHand(), new BlockHitResult(minecraft.player.position(), Direction.DOWN, minecraft.player.blockPosition().below(), false));
                }
            }
            while (NicetiesClient.nicetiesKey.consumeClick()) {
                minecraft.gui.setScreen(new NiceScreen(Component.literal("Niceties")));
            }
            while (NicetiesClient.autoClickKey.consumeClick()) {
                instance.autoClick = !instance.autoClick;
            }
            if (autoClick) {
                var player = minecraft.player;
                assert player != null;
                if (autoClickSpam) {
                    ((MinecraftAccessor)minecraft).niceties$startAttack();
                } else if (player.getAttackStrengthScale(0) == 1.0F) {
                    ((MinecraftAccessor)minecraft).niceties$startAttack();
                }
            }
        }));
    }

    public void saveESPBlocks() {
        Map<Identifier, CustomRenderPipeline.Color> saveFmt = new HashMap<>();
        for (Map.Entry<Block, CustomRenderPipeline.Color> entry : espBlocks.entrySet()) {
            saveFmt.put(entry.getKey().properties().blockId().identifier(), entry.getValue());
        }
        DataResult<JsonElement> serializeResult = ESP_CODEC.encodeStart(JsonOps.INSTANCE, saveFmt);

        JsonElement json = serializeResult.resultOrPartial(Niceties.LOGGER::error).orElseThrow();
        Niceties.LOGGER.info("serialized esp blocks: {}", json);
        try (FileWriter writer = new FileWriter(Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/config/niceties_esp.json")) {
            writer.write(json.toString());
        } catch (IOException e) {
            Niceties.LOGGER.error("error writing to esp config: {}", e.getMessage());
        }
    }

    public void loadESPBlocks(ClientLevel level) {
        try (FileReader reader = new FileReader(Minecraft.getInstance().gameDirectory + "/config/niceties_esp.json")) {
            var str = reader.readAllAsString();
            if (str.isBlank()) {
                Niceties.LOGGER.error("esp config file was blank, aborting read...");
            } else {
                Niceties.LOGGER.info("niceties_esp.json contents: {}", str);
                JsonElement json = JsonParser.parseString(str);
                DataResult<Map<Identifier, CustomRenderPipeline.Color>> deserializeResult = ESP_CODEC.parse(JsonOps.INSTANCE, json);
                var toConvert = deserializeResult.resultOrPartial(Niceties.LOGGER::error).orElseThrow();
                    var reg = level.registryAccess().lookupOrThrow(Registries.BLOCK);
                    for (var entry : toConvert.entrySet()) {
                        espBlocks.put(reg.get(entry.getKey()).orElseThrow().value(), entry.getValue());
                    }
            }
        } catch (IOException e) {
            Niceties.LOGGER.error("error reading esp blocks file: {}", e.getMessage());
        }
    }

    public void updateESPRate() {
        espHandle.cancel(false);
        espHandle = scheduler.scheduleAtFixedRate(new ESPScannerTask(), 0, this.espInterval, TimeUnit.MILLISECONDS);
    }

    public void toggleESP() {
        if (this.containerESP) {
            this.containerESP = false;
            ESPScannerTask.scan = false;
        } else {
            this.containerESP = true;
            ESPScannerTask.scan = true;
        }
    }

    public void shutdownESP() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    Niceties.LOGGER.error("esp scheduler did not terminate");
                }
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void sendChatMsg(String message) {
        Minecraft.getInstance().gui.hud.getChat().addRecentChat(message);
    }
    public void addMsgToQueue(String message) {
        System.out.println(message);
        msgq.add(message);
    }
}

