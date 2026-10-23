package com.hbm.machine.pilelegacy;

import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Genuine source ItemPileRod adds its shared instructions and per-rod description; it is not the MK2 depletion item. */
public final class LegacyPileRodItem extends Item {
    private final String rod;
    public LegacyPileRodItem(String rod){super(new Properties());this.rod=rod;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        for(String line:Language.getInstance().getOrDefault("desc.item.pileRod").split("\\$"))lines.add(Component.literal(line));
        for(String line:Language.getInstance().getOrDefault("item.pile_rod_"+rod+".desc").split("\\$"))lines.add(Component.literal(line));
    }
}
