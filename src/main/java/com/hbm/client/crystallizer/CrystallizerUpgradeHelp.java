package com.hbm.client.crystallizer;


import com.hbm.machine.crystallizer.*;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CrystallizerUpgradeHelp {
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event) {
        if(!(Minecraft.getInstance().screen instanceof CrystallizerScreen)) return;
        if(event.getItemStack().getItem() instanceof FurnaceUpgradeItem item&&item.type()==FurnaceUpgradeItem.Type.SPEED) {
            int level=item.tier(); var text=event.getToolTip(); text.add(Component.translatable("container.crystallizer").withStyle(ChatFormatting.YELLOW));
            if(item.type()==FurnaceUpgradeItem.Type.SPEED) { text.add(Component.translatable("upgrade.delay","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN)); text.add(Component.translatable("upgrade.consumption","+"+(level*100)+"%").withStyle(ChatFormatting.RED)); }
            else text.add(Component.translatable("upgrade.consumption","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));
        } else if(event.getItemStack().getItem() instanceof EffectUpgradeItem item){event.getToolTip().add(Component.translatable("container.crystallizer").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.translatable("upgrade.efficiency","x"+item.tier()).withStyle(ChatFormatting.GREEN));event.getToolTip().add(Component.translatable("upgrade.consumption","+"+(item.tier()*200)+"%").withStyle(ChatFormatting.RED));} else if(event.getItemStack().getItem() instanceof com.hbm.machine.centrifuge.OverdriveUpgradeItem) {
            event.getToolTip().add(Component.translatable("container.crystallizer").withStyle(ChatFormatting.YELLOW)); event.getToolTip().add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));
        }
    }
}
