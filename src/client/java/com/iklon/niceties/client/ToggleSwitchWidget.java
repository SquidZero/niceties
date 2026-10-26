package com.iklon.niceties.client;

import com.iklon.niceties.Niceties;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.dialog.Input;

import java.util.function.Supplier;

public class ToggleSwitchWidget extends Button {
    protected static final Button.CreateNarration DEFAULT_NARRATION = defaultNarrationSupplier -> (MutableComponent)defaultNarrationSupplier.get();

    Font font;

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(getX(), getY(), getX() + this.width+1, getY() + this.height+1, outlineColor);
        graphics.fill(getX(), getY(), getX() + this.width, getY() + this.height, boxColor);
        graphics.text(font, text, getX() + getWidth()/10, getY() + getHeight()/4, 0xffffffff, true);
        if (isHovered()) outlineColor = 0xffffffff; else outlineColor = 0xff2b2b2b;
    }

    public interface ToggleAction {
        void onPress(final Button button);
    }

    String text;
    ToggleAction action;
    public ToggleSwitchWidget(Font font, int x, int y, int width, int height, Component message, ToggleAction action) {
        super(x, y, width, height, Component.empty(), action::onPress, DEFAULT_NARRATION
        );
        this.text = message.getString();
        this.action = action;
        this.font = font;
        boxColor = 0xff636363;
        setText(text);
    }

    int outlineColor = 0xff2b2b2b;
    int boxColor = 0xff2b2b2b;

    @Override
    public void onPress(InputWithModifiers input) {
        action.onPress(this);
        boxColor = 0xff636363;
    }

//    @Override public void onRelease(InputWithModifiers input) {
//        boxColor = 0xff2b2b2b;
//    }

    public void setText(String newText) { this.text = newText; }
}

