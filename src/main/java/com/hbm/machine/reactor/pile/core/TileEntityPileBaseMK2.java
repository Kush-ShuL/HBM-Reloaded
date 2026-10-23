package com.hbm.machine.reactor.pile.core;
import net.minecraft.core.BlockPos;import net.minecraft.nbt.CompoundTag;import net.minecraft.world.level.block.state.BlockState;
/** Source detached parts restore graphite only when their actual controller chunk is loaded. */
public final class TileEntityPileBaseMK2 extends PileTickingBlockEntity {
 public TileEntityPileCore cachedCore;public int coreX,coreY=-999,coreZ;
 public TileEntityPileBaseMK2(BlockPos pos,BlockState state){super(PileCoreContent.PART.get(),pos,state);}
 public TileEntityPileBaseMK2 setCore(int x,int y,int z){coreX=x;coreY=y;coreZ=z;setChanged();return this;}
 @Override public void updateEntity(){if(!worldObj.isRemote&&coreY!=-999){var core=getCore();if((core==null||core.isInvalid())&&worldObj.chunkExists(coreX>>4,coreZ>>4))PileBodyBlock.restore(getLevel(),worldPosition,this);}}
 public TileEntityPileCore getCore(){if(cachedCore!=null&&!cachedCore.isInvalid())return cachedCore;if(worldObj.chunkExists(coreX>>4,coreZ>>4)&&worldObj.getTileEntity(coreX,coreY,coreZ) instanceof TileEntityPileCore core){cachedCore=core;return core;}return null;}
 @Override public void readFromNBT(CompoundTag tag){super.readFromNBT(tag);coreX=tag.getInt("cX");coreY=tag.contains("cY")?tag.getInt("cY"):-999;coreZ=tag.getInt("cZ");}
 @Override public void writeToNBT(CompoundTag tag){super.writeToNBT(tag);tag.putInt("cX",coreX);tag.putInt("cY",coreY);tag.putInt("cZ",coreZ);}
}
