package com.hbm.client.oilmixer;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** Original contextual IUpgradeInfoProvider descriptions, including the source blinking overdrive label. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MixerUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent e){if(!(Minecraft.getInstance().screen instanceof MixerScreen))return;var item=e.getItemStack().getItem();if(item instanceof FurnaceUpgradeItem u){e.getToolTip().add(Component.translatable("container.machineMixer").withStyle(ChatFormatting.YELLOW));if(u.type()==FurnaceUpgradeItem.Type.SPEED){e.getToolTip().add(Component.translatable("upgrade.delay","-"+(u.tier()*25)+"%").withStyle(ChatFormatting.GREEN));e.getToolTip().add(Component.translatable("upgrade.consumption","+"+(u.tier()*300)+"%").withStyle(ChatFormatting.RED));}else e.getToolTip().add(Component.translatable("upgrade.consumption","-"+(u.tier()*25)+"%").withStyle(ChatFormatting.GREEN));}else if(item instanceof OverdriveUpgradeItem){e.getToolTip().add(Component.translatable("container.machineMixer").withStyle(ChatFormatting.YELLOW));e.getToolTip().add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
}
