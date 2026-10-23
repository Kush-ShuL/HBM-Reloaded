package com.hbm.explosion.vanillant.standard;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
/** Exact old normal-cube outer rim conversion; source post phase is deliberately empty. */
public class BlockMutatorBulkie implements IBlockMutator {
 protected final BlockState replacement;
 public BlockMutatorBulkie(Block block){this(block.defaultBlockState());}public BlockMutatorBulkie(BlockState replacement){this.replacement=replacement;}
 @Override public void mutatePre(ExplosionVNT explosion,BlockState before,int meta,int x,int y,int z){var pos=new BlockPos(x,y,z);if(!LegacyBlockTraits.isNormalCube(explosion.world,pos,before))return;double dx=x+.5-explosion.posX,dy=y+.5-explosion.posY,dz=z+.5-explosion.posZ;if((double)(float)Math.sqrt(dx*dx+dy*dy+dz*dz)>=explosion.size-.5)explosion.world.setBlock(pos,replacement,3);}
 @Override public void mutatePost(ExplosionVNT explosion,int x,int y,int z){}
}
