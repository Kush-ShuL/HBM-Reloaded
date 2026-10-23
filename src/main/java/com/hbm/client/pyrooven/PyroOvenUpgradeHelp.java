package com.hbm.client.pyrooven;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** Source canProvideInfo only SPEED/POWER; the original OVERDRIVE description branch is unreachable. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PyroOvenUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof PyroOvenScreen)||!(event.getItemStack().getItem() instanceof FurnaceUpgradeItem upgrade))return;int level=upgrade.tier();var lines=event.getToolTip();lines.add(Component.translatable("container.machinePyroOven").withStyle(ChatFormatting.YELLOW));if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.delay","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(Math.pow(level+1,2)*100-100)+"%").withStyle(ChatFormatting.RED));}else lines.add(Component.translatable("upgrade.consumption","-"+(100-100/(level+1))+"%").withStyle(ChatFormatting.GREEN));}
}
