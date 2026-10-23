package com.hbm.machine.pa.cooled;
import com.hbm.machine.pa.physics.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public final class PaRfcEntity extends PaCooledEntity implements PaParticleUser {
 public static final long USAGE=250000;public PaRfcEntity(BlockPos pos,BlockState state){super(PaContent.ENTITY.get(),PaKind.RFC,pos,state);}
 private ForgeDirection axis(){return ForgeDirection.getOrientation(DummyableBlock.facing(getBlockState()).get3DDataValue()).getRotation(ForgeDirection.DOWN);}
 @Override public boolean canParticleEnter(PaParticle particle,ForgeDirection dir,int x,int y,int z){BlockPos pos=new BlockPos(x,y,z);var axis=axis();return pos.equals(worldPosition.offset(-axis.offsetX*4,0,-axis.offsetZ*4))&&axis==dir;}
 @Override public void onEnter(PaParticle particle,ForgeDirection dir){if(!isCool())particle.crash(PaState.CRASH_NOCOOL);if(!hasPower(USAGE))particle.crash(PaState.CRASH_NOPOWER);if(particle.invalid)return;particle.addDistance(9);particle.momentum+=100;particle.defocus(100);usePower(USAGE);}
 @Override public BlockPos getExitPos(PaParticle particle){var axis=axis();return worldPosition.offset(axis.offsetX*5,0,axis.offsetZ*5);}
}
