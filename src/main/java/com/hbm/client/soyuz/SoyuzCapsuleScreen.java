package com.hbm.client.soyuz;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.world.soyuz.SoyuzCapsuleMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original176x186 pixels; title center115/green7daf71 and inventory labelY92. */
public final class SoyuzCapsuleScreen extends LegacyContainerScreen<SoyuzCapsuleMenu> {
    public SoyuzCapsuleScreen(SoyuzCapsuleMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, ResourceLocation.parse("hbm:textures/gui/storage/gui_soyuz_capsule.png"), 176, 186);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 115 - font.width(title) / 2, 6, 0x7daf71, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4210752, false);
    }
}
