package com.hbm.client.tooltip;

import com.hbm.item.TooltipHints;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class MachineTooltips {
    private MachineTooltips() {}

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void append(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (!id.getNamespace().equals("hbm")) return;
        var lines = event.getToolTip();
        switch (id.getPath()) {
            case "heater_firebox", "heater_oilburner" -> {
                lines.add(Component.translatable("blocktag.heat_source").withStyle(ChatFormatting.RED));
                lines.add(Component.translatable("blocktag.polluting").withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("blocktag.exhaust_compatible").withStyle(ChatFormatting.GRAY));
            }
            case "heater_electric", "heater_heatex" ->
                    lines.add(Component.translatable("blocktag.heat_source").withStyle(ChatFormatting.RED));
            case "machine_bigasstank" -> storage(lines, "16,000,000", true, false);
            case "machine_orbus" -> storage(lines, "512,000", true, true);
            case "machine_fluidtank" -> storage(lines, "256,000", false, false);
            case "flow_control_pump", "network_separator" -> {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    lines.add(Component.translatable("tooltip.hbm." + id.getPath() + ".desc").withStyle(ChatFormatting.YELLOW));
                } else {
                    lines.add(TooltipHints.moreInfo());
                }
            }
        }
    }

    private static void storage(List<Component> lines, String capacity, boolean highlyCorrosive, boolean antimatter) {
        lines.add(Component.translatable("tooltip.hbm.barrel.capacity", capacity).withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("tooltip.hbm.barrel.hot").withStyle(ChatFormatting.GREEN));
        if (highlyCorrosive) {
            lines.add(Component.translatable("tooltip.hbm.barrel.highly_corrosive").withStyle(ChatFormatting.GREEN));
        } else {
            lines.add(Component.translatable("tooltip.hbm.barrel.corrosive").withStyle(ChatFormatting.GREEN));
            lines.add(Component.translatable("tooltip.hbm.barrel.no_highly_corrosive").withStyle(ChatFormatting.YELLOW));
        }
        lines.add(Component.translatable(antimatter ? "tooltip.hbm.barrel.antimatter" : "tooltip.hbm.barrel.no_antimatter")
                .withStyle(antimatter ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }
}
