package com.hbm.item.bobmazondeps;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original non-ticking TileEntitySnowglobe with byte-type save/description packet. */
public final class SnowglobeEntity extends BlockEntity {
 public TrinketTypes.Snowglobe type=TrinketTypes.Snowglobe.NONE;
 public SnowglobeEntity(BlockPos pos,BlockState state){super(BobmazonDependenciesContent.SNOW_ENTITY.get(),pos,state);}
 public void setType(int metadata){type=TrinketTypes.Snowglobe.metadata(metadata);setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putByte("type",(byte)type.ordinal());}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);type=TrinketTypes.Snowglobe.metadata(tag.getByte("type"));}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
