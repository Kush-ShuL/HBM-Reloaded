package com.hbm.client.toolbox;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.toolbox.ToolboxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class ToolboxScreen extends LegacyContainerScreen<ToolboxMenu> {
    public ToolboxScreen(ToolboxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ResourceLocation.parse("hbm:textures/gui/gui_toolbox.png"), 176, 211);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        var name = menu.box.displayName();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 37, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
}
