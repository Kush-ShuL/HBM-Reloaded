package com.hbm.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** X5846 standalone screens use the old dark gradient, without the modern menu blur pass. */
public abstract class LegacyScreen extends Screen {
    protected LegacyScreen(Component title) { super(title); }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // GuiScreen.drawScreen in 1.7.10 rendered widgets only. Modern Screen.render
        // also draws its background, which would darken the panel already drawn by
        // the original screen before its super call.
        for (var renderable : renderables) renderable.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, graphics));
    }

    @Override protected void renderBlurredBackground(float partialTick) { }
}
