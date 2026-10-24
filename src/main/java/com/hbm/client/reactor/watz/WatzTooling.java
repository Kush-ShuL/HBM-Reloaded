package com.hbm.client.reactor.watz;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.watz.WatzCasing;

import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** The same true BlockToolConversion BOLT recipe as the actual casing callback. */
final class WatzTooling {
    static List<ItemStack> bolts(){return com.hbm.registry.HbmItemTags.items("hbm:bolts/dura_steel").map(stack->stack.copyWithCount(4)).toList();}
    static List<ItemStack> tools(){return ToolableBlock.ToolType.BOLT.stacksForDisplay.stream().map(ItemStack::copy).toList();}
    static ItemStack input(){return WatzCasing.stack(0,1);}static ItemStack output(){return WatzCasing.stack(1,1);}
    private WatzTooling(){}
}
