package com.hbm.item.structurewand;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Fixed native identities for the source save and load controllers. */
public final class StructureAuthoringItem extends BlockItem {
    public StructureAuthoringItem(Block block,Properties properties){super(block,properties);}
    @Override public Component getName(ItemStack stack){return Component.translatable(((StructureAuthoringBlock)getBlock()).loading?"tile.wand_structure.load.name":"tile.wand_structure.save.name");}
}
