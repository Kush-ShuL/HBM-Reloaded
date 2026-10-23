package com.hbm.machine.reactor.pwr;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** The genuine portable ItemPWRFuel, separate from the existing world's fuel pillar block item. */
public final class PwrFuelItem extends Item {
    public PwrFuelItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public ItemStack stack(PwrFuelType fuel){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),fuel.ordinal());return stack;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.pwr_fuel."+PwrFuelType.from(metadata(stack)).serializedName()+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var fuel=PwrFuelType.from(metadata(stack));lines.add(Component.translatable("tooltip.hbm.pwr.heat",Component.literal(fuel.heatEmission+" TU").withStyle(ChatFormatting.RESET)).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.pwr.function",Component.literal(fuel.label()).withStyle(ChatFormatting.RESET)).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.pwr.fuel_type",fuel.danger()).withStyle(ChatFormatting.GOLD));}
}
