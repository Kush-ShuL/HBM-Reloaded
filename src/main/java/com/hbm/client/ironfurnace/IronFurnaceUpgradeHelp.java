package com.hbm.client.ironfurnace;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class IronFurnaceUpgradeHelp {
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(Minecraft.getInstance().screen instanceof IronFurnaceScreen&&event.getItemStack().getItem() instanceof FurnaceUpgradeItem item&&item.type()==FurnaceUpgradeItem.Type.SPEED){event.getToolTip().add(Component.translatable("block.hbm.furnace_iron").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.translatable("upgrade.delay","-"+(item.tier()*50/3)+"%").withStyle(ChatFormatting.GREEN));}}
    private IronFurnaceUpgradeHelp(){}
}
