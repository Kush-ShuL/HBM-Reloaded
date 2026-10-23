package com.hbm.client.compressor;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CompressorUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof CompressorScreen))return;if(event.getItemStack().getItem() instanceof FurnaceUpgradeItem item){int level=item.tier();var text=event.getToolTip();text.add(Component.translatable("container.machineCompressor").withStyle(ChatFormatting.YELLOW));if(item.type()==FurnaceUpgradeItem.Type.SPEED){text.add(Component.translatable("gui.hbm.compressor.generic_upgrade",Component.translatable("upgrade.delay","-"+(level==3?90:level==2?80:level==1?40:0)+"%")).withStyle(ChatFormatting.GREEN));text.add(Component.translatable("gui.hbm.compressor.recipe_upgrade",Component.translatable("upgrade.delay","-"+(100-100/(level+1))+"%")).withStyle(ChatFormatting.GREEN));}else text.add(Component.translatable("upgrade.consumption","-"+(100-100/(level+1))+"%").withStyle(ChatFormatting.GREEN));}else if(event.getItemStack().getItem() instanceof OverdriveUpgradeItem){event.getToolTip().add(Component.translatable("container.machineCompressor").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
}
