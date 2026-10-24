package com.hbm.machine.reactor.pwr;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Fixed source material/form; the existing reactor owns actual fission progress and temperatures. */
public final class PwrFuelItem extends Item {
    public enum Form {FRESH("pwr_fuel"),HOT("pwr_fuel_hot"),DEPLETED("pwr_fuel_depleted");public final String sourceName;Form(String sourceName){this.sourceName=sourceName;}}
    private final PwrFuelType fuel;
    private final Form form;
    public PwrFuelItem(PwrFuelType fuel,Form form){super(new Properties());this.fuel=java.util.Objects.requireNonNull(fuel);this.form=java.util.Objects.requireNonNull(form);}
    public PwrFuelType fuelType(){return fuel;}
    public Form form(){return form;}
    public static boolean isFresh(ItemStack stack){return stack.getItem() instanceof PwrFuelItem item&&item.form==Form.FRESH;}
    /** Existing reactor's loaded-material field remains real persistent machine state. */
    public static int metadata(ItemStack stack){if(!(stack.getItem() instanceof PwrFuelItem item))throw new IllegalArgumentException("Not a PWR fuel item");return item.fuel.ordinal();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+form.sourceName+"."+fuel.serializedName()+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(form!=Form.FRESH)return;lines.add(Component.translatable("tooltip.hbm.pwr.heat",Component.literal(fuel.heatEmission+" TU").withStyle(ChatFormatting.RESET)).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.pwr.function",Component.literal(fuel.label()).withStyle(ChatFormatting.RESET)).withStyle(ChatFormatting.GOLD));lines.add(Component.translatable("tooltip.hbm.pwr.fuel_type",fuel.danger()).withStyle(ChatFormatting.GOLD));}
}
