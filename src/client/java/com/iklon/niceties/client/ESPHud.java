package com.iklon.niceties.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class ESPHud {
    public static void extract(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        int color = 0xFFFF0000; // Red
        int targetColor = 0xFF00FF00; // Green

        // You can use the Util.getMillis() function to get the current time in milliseconds.
        // Divide by 1000 to get seconds.
        double currentTime = Util.getMillis() / 1000.0;

        // "lerp" simply means "linear interpolation", which is a fancy way of saying "blend".
        float lerpedAmount = Mth.abs(Mth.sin((float) currentTime));
        int lerpedColor = ARGB.linearLerp(lerpedAmount, color, targetColor);

        String blockScanTime = String.valueOf(ESPScannerTask.scanTime/1e6);

        // Draw a square with the lerped color.
        // x1, x2, y1, y2, color
        graphics.text(Minecraft.getInstance().font, blockScanTime, graphics.guiWidth()-20, 0, lerpedColor);

    }
}
