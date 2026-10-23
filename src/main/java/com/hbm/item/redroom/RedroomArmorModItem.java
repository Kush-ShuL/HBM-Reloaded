package com.hbm.item.redroom;
import com.hbm.armor.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual five source modifiers, real nine-slot armor storage, applicability and translated source lore. */
public final class RedroomArmorModItem extends ArmorModItem {
 public enum Kind {BALLISTIC_GAUNTLET("ballistic_gauntlet",ChatFormatting.YELLOW),POLISH("armor_polish",ChatFormatting.BLUE),BANDAID("bandaid",ChatFormatting.RED),QUARTZ("quartz_plutonium",ChatFormatting.DARK_GRAY),MILK("spider_milk",ChatFormatting.WHITE);public final String id;public final ChatFormatting color;Kind(String id,ChatFormatting color){this.id=id;this.color=color;}}
 public final Kind kind;
 public RedroomArmorModItem(Kind kind){super(kind==Kind.BALLISTIC_GAUNTLET?ArmorModStorage.SERVOS:ArmorModStorage.EXTRA,kind!=Kind.BALLISTIC_GAUNTLET,true,kind!=Kind.BALLISTIC_GAUNTLET,kind!=Kind.BALLISTIC_GAUNTLET);this.kind=kind;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+kind.id+".name");}
 public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.redroom."+kind.id+".installed",stack.getHoverName()).withStyle(kind.color);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){if(kind==Kind.BALLISTIC_GAUNTLET)lines.add(Component.translatable("tooltip.hbm.redroom.ballistic_gauntlet.quote").withStyle(ChatFormatting.ITALIC));lines.add(Component.translatable("tooltip.hbm.redroom."+kind.id+".effect").withStyle(kind.color));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flags);}
}
