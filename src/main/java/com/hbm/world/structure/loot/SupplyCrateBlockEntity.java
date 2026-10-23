package com.hbm.world.structure.loot;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Source TileEntitySupplyCrate has an unbounded persistent list, with no GUI or inventory capability. */
public final class SupplyCrateBlockEntity extends BlockEntity {
    public final List<ItemStack> items=new ArrayList<>();
    public SupplyCrateBlockEntity(BlockPos pos,BlockState state){super(StructureLootContent.SUPPLY_ENTITY.get(),pos,state);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var list=new ListTag();for(var stack:items)list.add(stack.saveOptional(lookup));tag.put("items",list);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);items.clear();for(var value:tag.getList("items",Tag.TAG_COMPOUND))items.add(ItemStack.parseOptional(lookup,(CompoundTag)value));}
}
