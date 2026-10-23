package com.hbm.client.radgen;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.radgen.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original176x184 screen,12 thin source progress strips and48px HE bar. */
public final class RadGenScreen extends LegacyContainerScreen<RadGenMenu> {
    public RadGenScreen(RadGenMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, ResourceLocation.parse("hbm:textures/gui/reactors/gui_radgen.png"), 176, 184); }
    private RadGenEntity machine() { return minecraft != null && minecraft.level != null && minecraft.level.getBlockEntity(menu.position()) instanceof RadGenEntity machine ? machine : null; }
    @Override protected void renderLabels(GuiGraphics graphics, int x, int y) { graphics.drawString(font, title, 88 - font.width(title) / 2, 6, 4210752, false); graphics.drawString(font, playerInventoryTitle, 8, 90, 4210752, false); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int x, int y) {
        super.renderBg(graphics, partial, x, y); var machine = machine(); if (machine == null) return;
        for (int i = 0; i < 12; i++) if (machine.maxProgress[i] > 0) drawLegacyRegion(graphics, 66, 19 + i * 5, 176, 0, machine.progress[i] * 44 / machine.maxProgress[i], 3);
        int width = machine.getPowerExact().multiply(java.math.BigInteger.valueOf(48)).divide(java.math.BigInteger.valueOf(RadGenEntity.MAX_POWER)).intValue();
        drawLegacyRegion(graphics, 64, 83, 176, 3, width, 4);
    }
    @Override public void render(GuiGraphics graphics, int x, int y, float partial) {
        super.render(graphics, x, y, partial); var machine = machine(); if (machine == null) return;
        if (legacyInfoHovered(x, y, 64, 83, 48, 4)) graphics.renderTooltip(font, Component.literal(BatteryItem.shortNumber(machine.getPower()) + "/" + BatteryItem.shortNumber(RadGenEntity.MAX_POWER) + "HE"), x, y);
        for (int i = 0; i < 12; i++) if (machine.maxProgress[i] > 0 && legacyInfoHovered(x, y, 65, 18 + i * 5, 46, 5)) {
            int remaining = machine.maxProgress[i] - machine.progress[i];
            graphics.renderComponentTooltip(font, List.of(Component.translatable("gui.hbm.radgen.slot", i + 1),
                Component.translatable("gui.hbm.radgen.production", machine.production[i]), Component.translatable("gui.hbm.radgen.remaining", remaining, remaining * 100 / machine.maxProgress[i])), x, y);
        }
    }
}
