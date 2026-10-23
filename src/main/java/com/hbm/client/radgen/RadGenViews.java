package com.hbm.client.radgen;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.radgen.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Viewer reads the one actual28-fuel execution table; source has no configurable RadGen recipe loader. */
final class RadGenViews {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");
    static ItemStack catalyst() { return new ItemStack(RadGenContent.ITEM.get()); }
    static void draw(RadGenFuels.Fuel fuel, GuiGraphics graphics, int x, int y) {
        com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics, TEXTURE, x, y);
        graphics.blit(TEXTURE, x + 47, y + 16, 5, 87, 18, 18, 256, 256);
        if (!fuel.outputStack().isEmpty()) graphics.blit(TEXTURE, x + 101, y + 16, 5, 87, 18, 18, 256, 256);
        var font = LegacyFonts.get();
        graphics.drawString(font, Component.translatable("jei.hbm.radgen.production", fuel.production()), x + 8, y + 43, 0x404040, false);
        graphics.drawString(font, Component.translatable("jei.hbm.radgen.duration", fuel.duration()), x + 8, y + 55, 0x404040, false);
    }
    private RadGenViews() {}
}
