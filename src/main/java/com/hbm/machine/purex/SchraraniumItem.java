package com.hbm.machine.purex;

import com.hbm.hazard.HazardRuntime;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemSchraranium: genuine shared LBSM mode changes icon/name and only that mode adds pankæk. */
public final class SchraraniumItem extends Item {
    public SchraraniumItem(){super(new Properties());}
    public static boolean nikonium(){return HazardRuntime.settings().lbsm();}
    @Override public Component getName(ItemStack stack){return nikonium()?Component.translatable("item.hbm.ingot_schraranium.nikonium"):super.getName(stack);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(nikonium())lines.add(Component.translatable("tooltip.hbm.schraranium.pancake"));}
}
