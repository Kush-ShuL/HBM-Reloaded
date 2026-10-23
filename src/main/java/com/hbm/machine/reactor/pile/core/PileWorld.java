package com.hbm.machine.reactor.pile.core;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
/** Direct calls into the actual level, retaining source coordinate/direction arithmetic. */
final class PileWorld {
 final Level nativeLevel;final boolean isRemote;final RandomSource rand;
 PileWorld(Level level){nativeLevel=level;isRemote=level.isClientSide;rand=level.random;}
 Block getBlock(int x,int y,int z){return nativeLevel.getBlockState(new BlockPos(x,y,z)).getBlock();}
 int getBlockMetadata(int x,int y,int z){return nativeLevel.getBlockState(new BlockPos(x,y,z)).getOptionalValue(PileCoreContent.META).orElse(0);}
 BlockEntity getTileEntity(int x,int y,int z){return nativeLevel.getBlockEntity(new BlockPos(x,y,z));}
 boolean chunkExists(int x,int z){return nativeLevel.hasChunk(x,z);}
 void setBlockMetadataWithNotify(int x,int y,int z,int metadata,int flags){var pos=new BlockPos(x,y,z);var state=nativeLevel.getBlockState(pos);nativeLevel.setBlock(pos,state.setValue(PileCoreContent.META,metadata),flags);}
 void setBlock(int x,int y,int z,Block block){nativeLevel.setBlockAndUpdate(new BlockPos(x,y,z),block.defaultBlockState());}
 long getTotalWorldTime(){return nativeLevel.getGameTime();}
 boolean getIndirectPowerOutput(int x,int y,int z,int side){return nativeLevel.getSignal(new BlockPos(x,y,z),Direction.values()[side])>0;}
 void playSoundEffect(double x,double y,double z,SoundEvent sound,float volume,float pitch){nativeLevel.playSound(null,x,y,z,sound,SoundSource.BLOCKS,volume,pitch);}
 void spawnEntityInWorld(Entity entity){nativeLevel.addFreshEntity(entity);}
 void newExplosion(Entity entity,double x,double y,double z,float strength,boolean fire,boolean terrain){nativeLevel.explode(entity,x,y,z,strength,fire,terrain?Level.ExplosionInteraction.BLOCK:Level.ExplosionInteraction.NONE);}
 void spawnParticle(String ignored,double x,double y,double z,double mx,double my,double mz){nativeLevel.addParticle(ParticleTypes.CLOUD,x,y,z,mx,my,mz);}
}
