package com.hbm.client.shredder;

import com.hbm.HbmReloaded;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.shredder.ShredderBlockEntity;
import com.hbm.machine.shredder.ShredderContent;
import com.hbm.machine.shredder.ShredderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Original 176x233 background, slot coordinates, progress UVs, blade icons and tooltip bounds. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ShredderScreen extends LegacyContainerScreen<ShredderMenu> {
    private static final ResourceLocation BACKGROUND = HbmReloaded.id("textures/gui/processing/gui_shredder.png");
    private static final ResourceLocation UTILITY = HbmReloaded.id("textures/gui/gui_utility.png");
    public ShredderScreen(ShredderMenu menu, Inventory player, Component title) { super(menu, player, title, BACKGROUND, 176, 233); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(ShredderContent.MENU.get(), ShredderScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 106 - font.width(title) / 2, 6, 4_210_752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4_210_752, false);
    }
    private boolean bladeError() { return menu.gearLeft() == 0 || menu.gearLeft() == 3 || menu.gearRight() == 0 || menu.gearRight() == 3; }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        super.renderBg(graphics, partial, mouseX, mouseY);
        if (menu.power() > 0) { int height = menu.scaledPower(88); drawLegacyRegion(graphics, 8, 106 - height, 176, 160 - height, 16, height); }
        drawLegacyRegion(graphics, 63, 89, 176, 54, menu.scaledProgress(34) + 1, 18);
        int left = menu.gearLeft(), right = menu.gearRight();
        if (left >= 1 && left <= 3) drawLegacyRegion(graphics, 43, 71, 176, (left - 1) * 18, 18, 18);
        if (right >= 1 && right <= 3) drawLegacyRegion(graphics, 79, 71, 194, (right - 1) * 18, 18, 18);
        if (bladeError()) drawTexturedModalRect(graphics, UTILITY, leftPos - 16, topPos + 36, 8, 16, 16, 16, 256, 256);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        super.render(graphics, mouseX, mouseY, partial);
        if (legacyInfoHovered(mouseX, mouseY, 8, 18, 16, 88)) graphics.renderTooltip(font, Component.literal(BatteryItem.shortNumber(menu.power()) + "/" + BatteryItem.shortNumber(ShredderBlockEntity.CAPACITY) + "HE"), mouseX, mouseY);
        if (bladeError() && legacyInfoHovered(mouseX, mouseY, -16, 36, 16, 16)) graphics.renderTooltip(font, Component.literal("Error: Shredder blades are broken or missing!"), leftPos - 8, topPos + 52);
    }
}
