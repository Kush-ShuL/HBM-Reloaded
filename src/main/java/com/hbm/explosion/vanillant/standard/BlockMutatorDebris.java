package com.hbm.explosion.vanillant.standard;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.explosion.LegacyExplosionMath;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
/** Source debris post-mutator: create rubble only beside a different normal-cube neighbor. */
public final class BlockMutatorDebris implements IBlockMutator {
 private final BlockState replacement;
 public BlockMutatorDebris(BlockState replacement){this.replacement=replacement;}
 @Override public void mutatePre(ExplosionVNT explosion,BlockState before,int meta,int x,int y,int z){}
 @Override public void mutatePost(ExplosionVNT explosion,int x,int y,int z){
  var pos=new BlockPos(x,y,z);
  for(var direction:Direction.values()){
   var neighbor=pos.relative(direction);var state=explosion.world.getBlockState(neighbor);
   if(LegacyBlockTraits.isNormalCube(explosion.world,neighbor,state)&&(state.getBlock()!=replacement.getBlock()||LegacyExplosionMath.metadata(state)!=LegacyExplosionMath.metadata(replacement))){explosion.world.setBlock(pos,replacement,3);return;}
  }
 }
}
