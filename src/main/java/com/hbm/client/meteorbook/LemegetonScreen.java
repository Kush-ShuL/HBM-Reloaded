package com.hbm.client.meteorbook;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.lemegeton.LemegetonMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original176x166 source GUI, exact result overlay and the existing unique original galactic font helper. */
public final class LemegetonScreen extends LegacyContainerScreen<LemegetonMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/gui/processing/gui_lemegeton.png");
    public LemegetonScreen(LemegetonMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, TEXTURE, 176, 166); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mx, int my) { super.renderBg(graphics, partial, mx, my); if (menu.getSlot(0).hasItem()) drawLegacyRegion(graphics, 7, 22, 0, 166, 162, 42); }
    @Override protected void renderLabels(GuiGraphics graphics, int mx, int my) {
        // The original encrypted glyphs use the literal English letters in every locale; translating changes the glyph program.
        BookGalacticLabels.draw(graphics, "Material Upgrade Conversion", 28, 6, 4210752);
        BookGalacticLabels.draw(graphics, "Standard Inventory", 8, imageHeight - 96 + 2, 4210752);
    }
}
