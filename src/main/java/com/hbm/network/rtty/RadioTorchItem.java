package com.hbm.network.rtty;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class RadioTorchItem extends BlockItem {
 public final boolean sender;
 public RadioTorchItem(boolean sender){super((sender?RadioTorchContent.SENDER:RadioTorchContent.RECEIVER).get(),new Properties());this.sender=sender;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(var line:Language.getInstance().getOrDefault("tile.radio_torch_"+(sender?"sender":"receiver")+".desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
