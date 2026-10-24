package com.hbm.client.reactor.iter.torus;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.iter.torus.FusionComponent;

import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** The same true BlockToolConversion TORCH recipe as the actual casing callback. */
final class TorusTooling {
    static List<ItemStack> bolts(){return com.hbm.registry.HbmItemTags.items("hbm:plates/cast/steel").map(stack->stack.copyWithCount(1)).toList();}
    static List<ItemStack> tools(){return ToolableBlock.ToolType.TORCH.stacksForDisplay.stream().map(ItemStack::copy).toList();}
    static ItemStack input(){return FusionComponent.stack(0,1);}static ItemStack output(){return FusionComponent.stack(1,1);}
    private TorusTooling(){}
}
