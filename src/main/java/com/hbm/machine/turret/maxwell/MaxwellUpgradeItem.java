package com.hbm.machine.turret.maxwell;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Both original ItemMachineUpgrade() SPECIAL identities, maxStack1/tier0; no invented charge or upgrade machine. */
public final class MaxwellUpgradeItem extends Item {
 private final boolean screm;
 public MaxwellUpgradeItem(boolean screm){super(new Properties().stacksTo(1));this.screm=screm;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
  if(screm){lines.add(Component.translatable("tooltip.hbm.maxwell.screm.header").withStyle(ChatFormatting.RED));for(int i=0;i<3;i++)lines.add(Component.translatable("tooltip.hbm.maxwell.screm."+i));}
 }
}
