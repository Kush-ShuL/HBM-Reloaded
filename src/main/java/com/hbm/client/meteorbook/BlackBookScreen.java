package com.hbm.client.meteorbook;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.meteorbook.BlackBookMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original GUIBook, including its actual galactic labels and result-dependent 54x54 grid overlay. */
public final class BlackBookScreen extends LegacyContainerScreen<BlackBookMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("hbm", "textures/gui/processing/gui_book.png");
    public BlackBookScreen(BlackBookMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, TEXTURE, 176, 166); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        super.renderBg(graphics, partial, mouseX, mouseY);
        if (menu.getSlot(0).hasItem()) drawLegacyRegion(graphics, 29, 16, 176, 0, 54, 54);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        BookGalacticLabels.draw(graphics, Component.translatable("gui.hbm.black_book.crafting").getString(), 28, 6, 4210752);
        BookGalacticLabels.draw(graphics, Component.translatable("gui.hbm.black_book.inventory").getString(), 8, imageHeight - 96 + 2, 4210752);
    }
}
