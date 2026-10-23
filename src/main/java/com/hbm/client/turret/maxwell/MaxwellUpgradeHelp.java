package com.hbm.client.turret.maxwell;
import com.hbm.registry.HbmContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** Exact IUpgradeInfoProvider behavior only in the actual Maxwell menu, not a second general upgrade system. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MaxwellUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
  if(!(Minecraft.getInstance().screen instanceof MaxwellTurretScreen))return;var item=event.getItemStack().getItem();
  for(String family:new String[]{"speed","effect","power","afterburn","overdrive"})for(int tier=1;tier<=3;tier++)if(item==HbmContent.item("upgrade_"+family+"_"+tier)){
   var lines=event.getToolTip();lines.add(Component.literal(">>> ").append(Component.translatable("tile.turret_maxwell.name")).append(" <<<").withStyle(ChatFormatting.YELLOW));
   switch(family){
    case "speed"->lines.add(Component.translatable("tooltip.hbm.maxwell.damage","0."+tier*25).withStyle(ChatFormatting.GREEN));
    case "power"->lines.add(Component.translatable("upgrade.consumption","-"+tier*3+"%").withStyle(ChatFormatting.GREEN));
    case "effect"->lines.add(Component.translatable("upgrade.range","+"+tier*3+"m").withStyle(ChatFormatting.GREEN));
    case "afterburn"->lines.add(Component.translatable("tooltip.hbm.maxwell.afterburn").withStyle(ChatFormatting.GREEN));
    case "overdrive"->lines.add(Component.translatable("tooltip.hbm.maxwell.overdrive").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));
   }return;
  }
 }
 private MaxwellUpgradeHelp(){}
}
