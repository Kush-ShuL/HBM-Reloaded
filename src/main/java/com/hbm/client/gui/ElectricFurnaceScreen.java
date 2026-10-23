package com.hbm.client.gui;

import com.hbm.HbmReloaded;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.electricfurnace.ElectricFurnaceBlockEntity;
import com.hbm.machine.electricfurnace.ElectricFurnaceContent;
import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Original 176 x 186 artwork and exact labels, slots, hover bounds and progress UVs. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ElectricFurnaceScreen extends LegacyContainerScreen<ElectricFurnaceMenu> {
    private static final ResourceLocation BACKGROUND = HbmReloaded.id("textures/gui/processing/gui_electric_furnace.png");
    private static final ResourceLocation UTILITY = HbmReloaded.id("textures/gui/gui_utility.png");
    public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, BACKGROUND, 176, 186); }
    @SubscribeEvent public static void registerScreens(RegisterMenuScreensEvent event) { event.register(ElectricFurnaceContent.MENU.get(), ElectricFurnaceScreen::new); }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 70 - font.width(title) / 2, 6, 4_210_752, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 4_210_752, false);
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        if (menu.power() >= menu.consumption()) {
            int power = menu.scaledPower(34);
            drawLegacyRegion(graphics, 152, 52 - power, 176, 64 - power, 16, power);
        }
        if (menu.active()) {
            drawLegacyRegion(graphics, 45, 20, 192, 12, 18, 16);
            drawLegacyRegion(graphics, 46, 47, 192, 28, 18, 16);
        }
        drawLegacyRegion(graphics, 43, 36, 176, 0, menu.scaledProgress(28), 12);
        drawTexturedModalRect(graphics, UTILITY, leftPos + 115, topPos + 19, 0, 32, 8, 8, 256, 256);
    }
    @Override protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> lines = new java.util.ArrayList<>(super.getTooltipFromContainerItem(stack));
        if (stack.getItem() instanceof FurnaceUpgradeItem upgrade) {
            lines.add(Component.literal(">>> ").append(Component.translatable("block.hbm.machine_electric_furnace_off")).append(" <<<").withStyle(ChatFormatting.YELLOW));
            int tier = upgrade.tier();
            if (upgrade.type() == FurnaceUpgradeItem.Type.SPEED) {
                lines.add(Component.translatable("upgrade.delay", "-" + tier * 25 + "%").withStyle(ChatFormatting.GREEN));
                lines.add(Component.translatable("upgrade.consumption", "+" + tier * 100 + "%").withStyle(ChatFormatting.RED));
            } else {
                lines.add(Component.translatable("upgrade.consumption", "-" + tier * 30 + "%").withStyle(ChatFormatting.GREEN));
                lines.add(Component.translatable("upgrade.delay", "+" + tier * 10 + "%").withStyle(ChatFormatting.RED));
            }
        }
        return lines;
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (legacyInfoHovered(mouseX, mouseY, 152, 18, 16, 34)) {
            graphics.renderTooltip(font, Component.literal(BatteryItem.shortNumber(menu.power()) + "/" + BatteryItem.shortNumber(ElectricFurnaceBlockEntity.CAPACITY) + "HE"), mouseX, mouseY);
        }
        if (legacyInfoHovered(mouseX, mouseY, 115, 19, 8, 8)) {
            graphics.renderComponentTooltip(font, List.of(Component.translatable("desc.gui.upgrade"), Component.translatable("desc.gui.upgrade.speed"), Component.translatable("desc.gui.upgrade.power")), mouseX, mouseY);
        }
    }
}
