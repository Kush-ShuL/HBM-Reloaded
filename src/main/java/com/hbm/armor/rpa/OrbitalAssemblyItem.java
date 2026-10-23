package com.hbm.armor.rpa;
import com.hbm.satellite.SatelliteFrequency;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source one-value ItemEnumMulti + ISatChip, not a satellite payload/relay or a plain recipe token. */
public final class OrbitalAssemblyItem extends Item implements SatelliteFrequency {
 public enum EnumOrbitalAssembly { CRYSTAL_CIRCUIT }
 public OrbitalAssemblyItem(){super(new Properties().stacksTo(1));}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.orbital_assembly.crystal_circuit.name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){lines.add(Component.translatable("satchip.frequency").append(": "+getFreq(stack)).withStyle(ChatFormatting.AQUA));}
}
