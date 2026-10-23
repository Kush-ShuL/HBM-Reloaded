package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
public class BlockMutatorFire implements IBlockMutator {
 @Override public void mutatePre(ExplosionVNT explosion,BlockState block,int meta,int x,int y,int z){}
 @Override public void mutatePost(ExplosionVNT explosion,int x,int y,int z){BlockPos pos=new BlockPos(x,y,z);if(explosion.world.isEmptyBlock(pos)&&explosion.world.getBlockState(pos.below()).isSolidRender(explosion.world,pos.below())&&explosion.world.random.nextInt(3)==0)explosion.world.setBlockAndUpdate(pos,Blocks.FIRE.defaultBlockState());}
}
