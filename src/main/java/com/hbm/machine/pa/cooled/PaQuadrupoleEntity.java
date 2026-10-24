package com.hbm.machine.pa.cooled;
import com.hbm.machine.pa.physics.*;
import com.hbm.machine.pa.parts.PaCoilItem;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public final class PaQuadrupoleEntity extends PaCooledEntity implements PaParticleUser {
 public static final long USAGE=100000;public PaQuadrupoleEntity(BlockPos pos,BlockState state){super(PaContent.ENTITY.get(),PaKind.QUADRUPOLE,pos,state);}
 private ForgeDirection axis(){return ForgeDirection.getOrientation(DummyableBlock.facing(getBlockState()).get3DDataValue()).getRotation(ForgeDirection.DOWN);}
 @Override public boolean canParticleEnter(PaParticle particle,ForgeDirection dir,int x,int y,int z){BlockPos pos=new BlockPos(x,y,z);var axis=axis();return pos.equals(worldPosition.offset(-axis.offsetX,0,-axis.offsetZ))&&axis==dir;}
 @Override public void onEnter(PaParticle particle,ForgeDirection dir){var coil=slots[1].getItem() instanceof PaCoilItem item?item.coil():null;int mult=coil!=null&&coil.quadMin>particle.momentum?10:1;if(!isCool())particle.crash(PaState.CRASH_NOCOOL);if(!hasPower(USAGE*mult))particle.crash(PaState.CRASH_NOPOWER);if(coil==null)particle.crash(PaState.CRASH_NOCOIL);if(coil!=null&&coil.quadMax<particle.momentum)particle.crash(PaState.CRASH_OVERSPEED);if(particle.invalid)return;particle.addDistance(3);particle.focus(100);usePower(USAGE*mult);}
 @Override public BlockPos getExitPos(PaParticle particle){var axis=axis();return worldPosition.offset(axis.offsetX*2,0,axis.offsetZ*2);}
}
