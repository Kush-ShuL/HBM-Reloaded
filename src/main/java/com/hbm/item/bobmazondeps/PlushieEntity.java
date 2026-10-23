package com.hbm.item.bobmazondeps;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityPlushie: only byte type persists; squish remains local transient state. */
public final class PlushieEntity extends BlockEntity {
 public TrinketTypes.Plushie type=TrinketTypes.Plushie.NONE;public int squishTimer;
 public PlushieEntity(BlockPos pos,BlockState state){super(BobmazonDependenciesContent.PLUSH_ENTITY.get(),pos,state);}
 public void tick(){if(squishTimer>0)squishTimer--;}
 public void setType(int metadata){type=TrinketTypes.Plushie.metadata(metadata);setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
 public static int transformedType(RandomSource worldRandom){return worldRandom.nextInt(TrinketTypes.Plushie.values().length-1)+1;}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putByte("type",(byte)type.ordinal());}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);type=TrinketTypes.Plushie.metadata(tag.getByte("type"));}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
