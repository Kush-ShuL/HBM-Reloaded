package com.hbm.client.mininglaser;

import com.hbm.item.recipedependencies.FortuneUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.crystallizer.EffectUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Original IUpgradeInfoProvider only inside this real machine's GUI; no replacement global upgrade hierarchy. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class MiningLaserUpgradeHelp {
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
        if(!(Minecraft.getInstance().screen instanceof MiningLaserScreen))return;
        var item=event.getItemStack().getItem();String type;int tier;
        if(item instanceof FurnaceUpgradeItem upgrade){type=upgrade.type()==FurnaceUpgradeItem.Type.SPEED?"speed":"power";tier=upgrade.tier();}
        else if(item instanceof EffectUpgradeItem upgrade){type="effect";tier=upgrade.tier();}
        else if(item instanceof FortuneUpgradeItem upgrade){type="fortune";tier=upgrade.tier();}
        else if(item instanceof OverdriveUpgradeItem upgrade){type="overdrive";tier=upgrade.tier();}
        else return;
        event.getToolTip().add(Component.translatable("tile.machine_mining_laser.name").withStyle(ChatFormatting.YELLOW));
        switch(type){
            case "speed"->{event.getToolTip().add(Component.translatable("upgrade.delay","-"+(100-100/(tier+1))+"%").withStyle(ChatFormatting.GREEN));event.getToolTip().add(Component.translatable("upgrade.consumption","+"+(100*tier/16)+"%").withStyle(ChatFormatting.RED));}
            case "power"->event.getToolTip().add(Component.translatable("upgrade.consumption","-"+(100*tier/16)+"%").withStyle(ChatFormatting.GREEN));
            case "effect"->event.getToolTip().add(Component.translatable("upgrade.range","+"+(2*tier)+"m").withStyle(ChatFormatting.GREEN));
            case "fortune"->event.getToolTip().add(Component.translatable("upgrade.fortune","+"+tier).withStyle(ChatFormatting.GREEN));
            case "overdrive"->event.getToolTip().add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));
            default->{}
        }
    }
    private MiningLaserUpgradeHelp(){}
}
