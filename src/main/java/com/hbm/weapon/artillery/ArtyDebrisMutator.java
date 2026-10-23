package com.hbm.weapon.artillery;
import com.hbm.block.LegacyBlockTraits;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original BlockMutatorDebris, confined to this family without changing the shared explosion engine. */
public final class ArtyDebrisMutator implements IBlockMutator {
    private final BlockState debris;
    public ArtyDebrisMutator(BlockState debris){this.debris=debris;}
    @Override public void mutatePre(ExplosionVNT explosion,BlockState old,int meta,int x,int y,int z){}
    @Override public void mutatePost(ExplosionVNT explosion,int x,int y,int z){var pos=new BlockPos(x,y,z);for(var direction:Direction.values()){var next=pos.relative(direction);var state=explosion.world.getBlockState(next);if(LegacyBlockTraits.isNormalCube(explosion.world,next,state)&&state!=debris){explosion.world.setBlock(pos,debris,3);return;}}}
}
