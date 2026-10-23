package com.hbm.client.electrolyser;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ElectrolyserUpgradeHelp {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){if(!(Minecraft.getInstance().screen instanceof ElectrolyserScreen))return;var item=event.getItemStack().getItem();if(item instanceof FurnaceUpgradeItem upgrade){var lines=event.getToolTip();int level=upgrade.tier();lines.add(Component.translatable("container.machineElectrolyser").withStyle(ChatFormatting.YELLOW));if(upgrade.type()==FurnaceUpgradeItem.Type.SPEED){lines.add(Component.translatable("upgrade.delay","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.consumption","+"+(level*100)+"%").withStyle(ChatFormatting.RED));}else{lines.add(Component.translatable("upgrade.consumption","-"+(level*25)+"%").withStyle(ChatFormatting.GREEN));lines.add(Component.translatable("upgrade.delay","+25%").withStyle(ChatFormatting.RED));}}else if(item instanceof OverdriveUpgradeItem){event.getToolTip().add(Component.translatable("container.machineElectrolyser").withStyle(ChatFormatting.YELLOW));event.getToolTip().add(Component.translatable("gui.hbm.electrolyser.yes").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_GRAY));}}
 private ElectrolyserUpgradeHelp(){}
}
