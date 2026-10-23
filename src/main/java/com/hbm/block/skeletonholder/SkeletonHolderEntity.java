package com.hbm.block.skeletonholder;
import com.hbm.world.structure.loot.LegacyStructureItems;
import java.util.HashMap;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact singular item field, no ticking/inventory capability/HE. Shared by the real DungeonSpawner reward. */
public final class SkeletonHolderEntity extends BlockEntity {
 public ItemStack item=ItemStack.EMPTY;
 public SkeletonHolderEntity(BlockPos pos,BlockState state){super(SkeletonHolderContent.ENTITY.get(),pos,state);}
 public void setItem(ItemStack stack){item=stack;setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(!item.isEmpty())tag.put("item",item.save(lookup));}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);var stored=tag.getCompound("item");if(stored.isEmpty()){item=ItemStack.EMPTY;return;}if(stored.contains("Count")){var palette=new HashMap<Integer,String>();for(var entry:tag.getList("hbm_skeleton_item_palette",Tag.TAG_COMPOUND)){var row=(CompoundTag)entry;palette.put(row.getInt("id"),row.getString("name"));}item=LegacyStructureItems.convertStack(stored,palette,lookup);}else item=ItemStack.parseOptional(lookup,stored);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
