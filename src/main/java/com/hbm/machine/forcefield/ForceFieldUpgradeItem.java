package com.hbm.machine.forcefield;
import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Real source SPECIAL ItemMachineUpgrade radius/health identities, stack16 and exact three-line fallback help. */
public final class ForceFieldUpgradeItem extends Item {
 private final boolean radius;public ForceFieldUpgradeItem(boolean radius){super(new Properties().stacksTo(16));this.radius=radius;}
 public boolean radius(){return radius;}
 @Override public Component getName(ItemStack stack){return Component.translatable(radius?"item.upgrade_radius.name":"item.upgrade_health.name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){String key=radius?"radius":"health";lines.add(Component.translatable("tooltip.hbm.forcefield."+key+"_upgrade").withStyle(ChatFormatting.RED));lines.add(Component.translatable("tooltip.hbm.forcefield."+key+"_stats"));lines.add(Component.translatable("tooltip.hbm.forcefield.stack16"));}
}
