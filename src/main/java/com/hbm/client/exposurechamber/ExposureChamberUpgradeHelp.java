package com.hbm.client.exposurechamber;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** All three actual IUpgradeInfoProvider branches, including the original blinking OVERDRIVE YES. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ExposureChamberUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof ExposureChamberScreen))return;var item=event.getItemStack().getItem();var lines=event.getToolTip();if(item instanceof FurnaceUpgradeItem upgrade){int level=upgrade.tier();lines.add(Component.literal(">>> ").append(Component.translatable("container.exposureChamber")).append(" <<<").withStyle(ChatFormatting.YELLOW));if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.delay","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(level*50)+"%").withStyle(ChatFormatting.RED));}else{lines.add(Component.translatable("upgrade.consumption","-"+(100-100/(level+1))+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.delay","+"+(level*50)+"%").withStyle(ChatFormatting.RED));}}else if(item instanceof OverdriveUpgradeItem){lines.add(Component.literal(">>> ").append(Component.translatable("container.exposureChamber")).append(" <<<").withStyle(ChatFormatting.YELLOW));lines.add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
}
