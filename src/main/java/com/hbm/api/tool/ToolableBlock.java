package com.hbm.api.tool;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Native source IToolable, retaining all six original tool kinds and registered display stacks. */
public interface ToolableBlock {
    boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float hitX,float hitY,float hitZ,ToolType tool);
    enum ToolType {
        SCREWDRIVER,HAND_DRILL,DEFUSER,WRENCH,TORCH,BOLT;
        public final List<ItemStack> stacksForDisplay=new ArrayList<>();
        public void register(ItemStack stack){stacksForDisplay.add(stack.copy());}
        public static ToolType getType(ItemStack stack){for(var type:values())for(var registered:type.stacksForDisplay)if(registered.getItem()==stack.getItem()&&registered.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),registered.getDamageValue()).equals(stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())))return type;return null;}
    }
}
