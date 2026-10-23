package com.hbm.block.light.floodlight;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original5tick owner/cache/index validation. The persisted source coordinate now correctly identifies its owner instead of the beam itself. */
public final class FloodlightBeamEntity extends BlockEntity{
 public FloodlightBlockEntity cache;private BlockPos source;public int index;
 public FloodlightBeamEntity(BlockPos pos,BlockState state){super(FloodlightContent.BEAM_ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,FloodlightBeamEntity beam){if(level.isClientSide||level.getGameTime()%5!=0)return;if(beam.source==null||beam.index<0||beam.index>=15){level.setBlock(pos,Blocks.AIR.defaultBlockState(),2);return;}if(beam.cache==null&&level.hasChunkAt(beam.source)){if(level.getBlockEntity(beam.source)instanceof FloodlightBlockEntity lamp)beam.cache=lamp;else level.setBlock(pos,Blocks.AIR.defaultBlockState(),2);}if(beam.cache!=null&&(beam.cache.isRemoved()||!beam.cache.isOn||!pos.equals(beam.cache.lightPos[beam.index])))level.setBlock(pos,Blocks.AIR.defaultBlockState(),2);}
 public void setSource(FloodlightBlockEntity owner,int index){cache=owner;source=owner.getBlockPos().immutable();this.index=index;setChanged();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(source!=null){tag.putInt("sourceX",source.getX());tag.putInt("sourceY",source.getY());tag.putInt("sourceZ",source.getZ());tag.putInt("index",index);}}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);source=tag.contains("sourceX",Tag.TAG_ANY_NUMERIC)&&tag.contains("sourceY",Tag.TAG_ANY_NUMERIC)&&tag.contains("sourceZ",Tag.TAG_ANY_NUMERIC)?new BlockPos(tag.getInt("sourceX"),tag.getInt("sourceY"),tag.getInt("sourceZ")):null;index=tag.getInt("index");cache=null;}
}
