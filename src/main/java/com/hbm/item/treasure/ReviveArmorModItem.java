package com.hbm.item.treasure;

import com.hbm.armor.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemModRevive, leggings-only extra slot; damage is remaining genuine revives. */
public final class ReviveArmorModItem extends ArmorModItem {
    private final String name;
    public ReviveArmorModItem(String name,int durability){super(new Item.Properties().stacksTo(1).durability(durability),ArmorModStorage.EXTRA,false,false,true,false);this.name=name;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.revive_mod.installed",stack.getHoverName(),stack.getMaxDamage()-stack.getDamageValue()).withStyle(ChatFormatting.GOLD);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(name.equals("scrumpy")){tooltip.add(Component.translatable("tooltip.hbm.revive_mod.scrumpy.0").withStyle(ChatFormatting.GOLD));tooltip.add(Component.translatable("tooltip.hbm.revive_mod.scrumpy.1").withStyle(ChatFormatting.RED));}
        else tooltip.add(Component.translatable("tooltip.hbm.revive_mod.wild").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.empty());tooltip.add(Component.translatable("tooltip.hbm.revive_mod.remaining",stack.getMaxDamage()-stack.getDamageValue()).withStyle(ChatFormatting.GOLD));tooltip.add(Component.empty());super.appendHoverText(stack,context,tooltip,flag);
    }
}
