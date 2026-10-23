package com.hbm.item.treasure;

import com.hbm.armor.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemModHealth family, chest-only extra modification with actual max-health attribute. */
public final class HealthArmorModItem extends ArmorModItem {
    private final String name;private final float health;
    public HealthArmorModItem(String name,float health){super(ArmorModStorage.EXTRA,false,true,false,false);this.name=name;this.health=health;}
    public float health(){return health;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    private static ChatFormatting color(){return System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.LIGHT_PURPLE;}
    private String displayHealth(){return Double.toString(Math.round(health*10)*.1D);}
    public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.health_mod.installed",stack.getHoverName(),displayHealth()).withStyle(color());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.health_mod.health",displayHealth()).withStyle(color()));tooltip.add(Component.empty());if(name.equals("black_diamond")){tooltip.add(Component.translatable("tooltip.hbm.health_mod.nostalgia").withStyle(ChatFormatting.DARK_GRAY));tooltip.add(Component.empty());}super.appendHoverText(stack,context,tooltip,flag);}
}
