package com.iklon.niceties.client.mixin;
import com.iklon.niceties.client.NicetiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "insertText", at = @At(value = "HEAD"), cancellable = true)
    void processChatMsg(String text, boolean replace, CallbackInfo ci) {
        if (text.startsWith(";") && !replace && text.length() > 1) {
            text = text.split(";")[1];
            System.out.println("processing " + text);
            processCmd(text);
            ci.cancel();
        }
    }

    @Unique
    void processCmd(String cmd) {
        if (Objects.equals(cmd, "maceBlock")) {
            assert Minecraft.getInstance().player != null;
            LocalPlayer player = Minecraft.getInstance().player;
            ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
            if (!player.getItemInHand(player.getUsedItemHand()).is(Item.byId(0))) {
                try {
                    NicetiesClient.instance.maceBlock = Block.byItem(player.getItemInHand(player.getUsedItemHand()).getItem());
                    chat.addRecentChat(NicetiesClient.instance.maceBlock.toString() + " selected");
                } catch (Exception e) {
                    System.out.println(e);
                    chat.addRecentChat("invalid block!");
                }
            } else {
                chat.addRecentChat("no item selected");
            }
        } else if (cmd.startsWith("esp ")) {
            cmd = cmd.split("esp ", 1)[1];
            if (cmd.startsWith("add ")) {
                cmd = cmd.split("add ", 1)[1];

            }
        }
    }

}
