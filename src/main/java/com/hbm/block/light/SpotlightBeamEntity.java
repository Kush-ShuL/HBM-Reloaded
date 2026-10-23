package com.hbm.block.light;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Exact TileEntityData: source meta integer stores six independent incoming-direction bits. */
public final class SpotlightBeamEntity extends BlockEntity {
 public int metadata;
 public SpotlightBeamEntity(BlockPos pos,BlockState state){super(SourceLightContent.BEAM_ENTITY.get(),pos,state);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("meta",metadata);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);metadata=tag.getInt("meta");}
}
