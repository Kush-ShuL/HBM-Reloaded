package com.hbm.client.plasticbag;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.plasticbag.PlasticBagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** GUIPlasticBag draws only the original inventory label, not a new container heading. */
public final class PlasticBagScreen extends LegacyContainerScreen<PlasticBagMenu> {
    public PlasticBagScreen(PlasticBagMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ResourceLocation.parse("hbm:textures/gui/storage/gui_plastic_bag.png"), 176, 216);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
}
