package com.hbm.client.chemicalfactory;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** Actual IUpgradeInfoProvider labels, retaining the source tooltip/consumption-factor discrepancy. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ChemicalFactoryUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof ChemicalFactoryScreen))return;var item=event.getItemStack().getItem();if(item instanceof FurnaceUpgradeItem upgrade){int level=upgrade.tier();var lines=event.getToolTip();lines.add(Component.translatable("container.machineChemicalFactory").withStyle(ChatFormatting.YELLOW));if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.speed","+"+(level*100/3)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(level*50)+"%").withStyle(ChatFormatting.RED));}else lines.add(Component.translatable("upgrade.consumption","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));}else if(item instanceof OverdriveUpgradeItem){event.getToolTip().add(Component.translatable("container.machineChemicalFactory").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.literal("YES").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
}
