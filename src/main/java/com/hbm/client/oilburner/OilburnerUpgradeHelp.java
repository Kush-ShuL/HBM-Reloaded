package com.hbm.client.oilburner;

import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** The oilburner's X5863 overdrive-only upgrade information. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class OilburnerUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
  if(!(Minecraft.getInstance().screen instanceof OilburnerScreen)||!(event.getItemStack().getItem() instanceof OverdriveUpgradeItem))return;
  event.getToolTip().add(Component.literal(">>> ").append(Component.translatable("container.heaterOilburner")).append(" <<<").withStyle(ChatFormatting.YELLOW));
  event.getToolTip().add(Component.translatable("gui.yes").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));
 }
 private OilburnerUpgradeHelp(){}
}
