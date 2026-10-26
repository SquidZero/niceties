package com.iklon.niceties.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static com.iklon.niceties.client.NicetiesClient.*;

public class NiceScreen extends Screen {
    public NiceScreen(Component description) {
        super(description);
    }

    EditBox veloinput;
    EditBox numAtksInput;

    String checkNuke() { if (instance.nuke) return "ON"; else return "OFF"; }
    String checkWater() { if (instance.waterPhysics) return "ON"; else return "OFF"; }
    String checkNoSlow() { if (instance.noSlow) return "ON"; else return "OFF"; }
    String checkPlayerGlow() {if (instance.playerGlow) return "ON"; else return "OFF"; }
    String checkESP() { if (instance.containerESP) return "ON"; else return "OFF"; }
    String checkElytraFly() { if (instance.elytraFly) return "ON"; else return "OFF"; }

    String checkEnabled(boolean enabled) { if (enabled) return "ON"; else return "OFF"; }


    @Override
    protected void init() {
        veloinput = new EditBox(this.font, 10, 60, 55, 20, this.getNarrationMessage());
        numAtksInput = new EditBox(this.font, 10, 30, 55, 20, this.getNarrationMessage());
        numAtksInput.insertText(String.valueOf(instance.numAtks));
        veloinput.insertText(String.valueOf(NicetiesClient.instance.addedVel));

        Button closeButton = Button.builder(Component.nullToEmpty("close"), (btn) -> {
            this.onClose();
        }).bounds(10,80,80, 20).build();

        WidgetSprites resetVel = new WidgetSprites(Identifier.withDefaultNamespace("icon/accessibility"));

        Button resetVelocityBtn = new ImageButton(16, 16, resetVel, (Button) -> {
            instance.addedVel = 1;
        }, Component.empty());
        resetVelocityBtn.setPosition(70, 60);//.setDimensionsAndPosition(20, 20, 70, 50);

        final ToggleSwitchWidget[] nukeButton = new ToggleSwitchWidget[1];
        nukeButton[0] = new ToggleSwitchWidget(this.getFont(), 100, 50 ,60 ,20, Component.literal("Nuker: " + checkNuke()), (button) -> {
            instance.nuke = !instance.nuke;
            nukeButton[0].setText("Nuker: " + checkNuke());
        });

        final ToggleSwitchWidget[] waterBtn = new ToggleSwitchWidget[1];
        waterBtn[0] = new ToggleSwitchWidget(this.getFont(),100, 80, 100, 20, Component.literal("Water Physics: " + checkWater()), (button) -> {
            instance.waterPhysics = !instance.waterPhysics;
            waterBtn[0].setText("Water Physics: " + checkWater());
        });

        final ToggleSwitchWidget[] noSlowBtn = new ToggleSwitchWidget[1];
        noSlowBtn[0] = new ToggleSwitchWidget(this.getFont(),100, 110, 70, 20, Component.literal("No Slow: " + checkNoSlow()), (button) -> {
            instance.noSlow = !instance.noSlow;
            noSlowBtn[0].setText("No Slow: " + checkNoSlow());
        });

        final ToggleSwitchWidget[] playerGlowBtn = new ToggleSwitchWidget[1];
        playerGlowBtn[0] = new ToggleSwitchWidget(this.getFont(), 100, 140 ,100, 20, Component.literal("Player Glow: " + checkPlayerGlow()), (button) -> {
            instance.playerGlow = !instance.playerGlow;
            playerGlowBtn[0].setText("Player Glow: " + checkPlayerGlow());
        });

        final ToggleSwitchWidget[] espBtn = new ToggleSwitchWidget[1];
        espBtn[0] = new ToggleSwitchWidget(this.getFont(), 100, 170 ,50, 20, Component.literal("ESP: " + checkESP()), (button) -> {
            instance.toggleESP();
            espBtn[0].setText("ESP: " + checkESP());
        });

        final NiceSlider[] espRange = new NiceSlider[1];
        espRange[0] = new NiceSlider(font, 100, 200, 100, 20, Component.literal("esp range"), instance.espDistance, 16, 3200, 0, value -> {
            instance.espDistance = (int)value;
            espRange[0].updateMessage();
        });

        final NiceSlider[] espInterval = new NiceSlider[1];
        espInterval[0] = new NiceSlider(font, 220, 200, 90, 20, Component.literal("ESP Interval"), instance.espInterval, 1, 10000, 0, value -> {
            instance.espInterval = (long)value + 1;
            instance.updateESPRate();
            espInterval[0].updateMessage();
        });

        final ToggleSwitchWidget[] elytraFlyBtn = new ToggleSwitchWidget[1];
        elytraFlyBtn[0] = new ToggleSwitchWidget(this.getFont(), 170, 50, 90, 20, Component.literal("Elytra Fly: " + checkElytraFly()), (button) -> {
            instance.elytraFly = !instance.elytraFly;
            elytraFlyBtn[0].setText("Elytra Fly: " + checkElytraFly());
        });

        final ToggleSwitchWidget[] fastMineBtn = new ToggleSwitchWidget[1];
        fastMineBtn[0] = new ToggleSwitchWidget(this.getFont(), 210, 80, 90, 20, Component.literal("Fast Mine: " + checkEnabled(instance.fastMine)), (button) -> {
            instance.fastMine = !instance.fastMine;
            fastMineBtn[0].setText("Fast Mine: " + checkEnabled(instance.fastMine));
        });

        final ToggleSwitchWidget[] invMoveBtn = new ToggleSwitchWidget[1];
        invMoveBtn[0] = new ToggleSwitchWidget(this.getFont(), 180, 110, 80, 20, Component.literal("Gui Move: " + checkEnabled(instance.invMove)), (button) -> {
            instance.invMove = !instance.invMove;
            invMoveBtn[0].setText("InvMove: " + checkEnabled(instance.invMove));
        });

        final ToggleSwitchWidget[] preventDropBtn = new ToggleSwitchWidget[1];
        preventDropBtn[0] = new ToggleSwitchWidget(this.getFont(), 210, 140, 100, 20, Component.literal("Prevent Drop: " + checkEnabled(instance.preventDrop)), (button) -> {
            instance.preventDrop = !instance.preventDrop;
            preventDropBtn[0].setText("Prevent Drop: " + checkEnabled(instance.preventDrop));
        });

        final ToggleSwitchWidget[] autoClickBtn = new ToggleSwitchWidget[1];
        autoClickBtn[0] = new ToggleSwitchWidget(this.getFont(), 210, 170, 100, 20, Component.literal("Auto Click: " + checkEnabled(instance.autoClick)), (button) -> {
            instance.autoClick = !instance.autoClick;
            autoClickBtn[0].setText("Auto Click: " + checkEnabled(instance.autoClick));
        });

        final ToggleSwitchWidget[] autoClickSpamBtn = new ToggleSwitchWidget[1];
        autoClickSpamBtn[0] = new ToggleSwitchWidget(this.getFont(), 330, 170, 50, 20, Component.literal("Spam: " + checkEnabled(instance.autoClickSpam)), (button) -> {
            instance.autoClickSpam = !instance.autoClickSpam;
            autoClickSpamBtn[0].setText("Spam: " + checkEnabled(instance.autoClickSpam));
        });

        this.addRenderableWidget(veloinput);
        this.addRenderableWidget(numAtksInput);
        this.addRenderableWidget(resetVelocityBtn);
        this.addRenderableWidget(nukeButton[0]);
        this.addRenderableWidget(waterBtn[0]);
        this.addRenderableWidget(noSlowBtn[0]);
        this.addRenderableWidget(playerGlowBtn[0]);
        this.addRenderableWidget(espBtn[0]);
        this.addRenderableWidget(espRange[0]);
        this.addRenderableWidget(espInterval[0]);
        this.addRenderableWidget(elytraFlyBtn[0]);
        this.addRenderableWidget(fastMineBtn[0]);
        this.addRenderableWidget(invMoveBtn[0]);
        this.addRenderableWidget(preventDropBtn[0]);
        this.addRenderableWidget(autoClickBtn[0]);
        this.addRenderableWidget(autoClickSpamBtn[0]);
        this.addRenderableWidget(closeButton);

    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.text(this.font, "velocity", 10, 50 - this.font.lineHeight, 0xffffffff, true);
        graphics.text(this.font, "Num Atks", 10, 20 - this.font.lineHeight, 0xffffffff, true);
    }

    @Override public void onClose() {
        String velinp = veloinput.getValue();
        String numAtksinp = numAtksInput.getValue();
        if (!velinp.isEmpty()) instance.addedVel = Double.parseDouble(velinp);
        if (!numAtksinp.isEmpty()) instance.numAtks = Integer.parseInt(numAtksinp);
        Minecraft.getInstance().setScreen(null);
    }
}
