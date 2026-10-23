package com.hbm.entity.mob.maskman;
import com.hbm.armor.*;import com.hbm.interfaces.IArmorModDash;import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Original ItemModCloud, plate-only chest attachment; live speed and dash execution are in CloudDashRuntime. */
public final class BottledCloudItem extends ArmorModItem implements IArmorModDash {
 public BottledCloudItem(){super(ArmorModStorage.CHEST,false,true,false,false);}@Override public int getDashes(){return 3;}public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.bottled_cloud.installed",stack.getHoverName()).withStyle(ChatFormatting.RED);}@Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.bottled_cloud.dash").withStyle(ChatFormatting.WHITE));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flag);}
}
