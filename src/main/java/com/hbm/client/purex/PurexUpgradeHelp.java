package com.hbm.client.purex;

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
public final class PurexUpgradeHelp {
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event) {
        if(!(Minecraft.getInstance().screen instanceof PurexScreen)) return;
        if(event.getItemStack().getItem() instanceof FurnaceUpgradeItem item) {
            int level=item.tier(); var text=event.getToolTip(); text.add(Component.translatable("container.machinePUREX").withStyle(ChatFormatting.YELLOW));
            if(item.type()==FurnaceUpgradeItem.Type.SPEED) { text.add(Component.translatable("upgrade.speed","+"+(level*100/3)+"%").withStyle(ChatFormatting.GREEN)); text.add(Component.translatable("upgrade.consumption","+"+(level*50)+"%").withStyle(ChatFormatting.RED)); }
            else text.add(Component.translatable("upgrade.consumption","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));
        } else if(event.getItemStack().getItem() instanceof OverdriveUpgradeItem) {
            event.getToolTip().add(Component.translatable("container.machinePUREX").withStyle(ChatFormatting.YELLOW)); event.getToolTip().add(Component.translatable("gui.hbm.purex.overdrive").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));
        }
    }
}
