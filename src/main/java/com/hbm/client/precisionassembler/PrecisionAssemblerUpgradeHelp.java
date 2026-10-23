package com.hbm.client.precisionassembler;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
/** Original IUpgradeInfoProvider formulas, including source +50% help vs +100% runtime SPEED power quirk. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class PrecisionAssemblerUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof PrecisionAssemblerScreen))return;var item=event.getItemStack().getItem();if(item instanceof FurnaceUpgradeItem upgrade){var lines=event.getToolTip();int level=upgrade.tier();lines.add(Component.translatable("container.machinePrecAss").withStyle(ChatFormatting.YELLOW));if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.speed","+"+(level*100/3)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(level*50)+"%").withStyle(ChatFormatting.RED));}else lines.add(Component.translatable("upgrade.consumption","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));}else if(item instanceof OverdriveUpgradeItem){event.getToolTip().add(Component.translatable("container.machinePrecAss").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.translatable("gui.hbm.precass.yes").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
 private PrecisionAssemblerUpgradeHelp(){}
}
