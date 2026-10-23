package com.hbm.explosion.vanillant.standard;

import com.hbm.bomb.nuclear.NuclearSpecialContent;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Original post-blast balefire placement, shared by the actual VNT processor. */
public final class BlockMutatorBalefire implements IBlockMutator {
    @Override public void mutatePre(ExplosionVNT explosion, BlockState block, int meta, int x, int y, int z) {}

    @Override public void mutatePost(ExplosionVNT explosion, int x, int y, int z) {
        var pos = new BlockPos(x, y, z);
        var below = pos.below();
        if (explosion.world.isEmptyBlock(pos)
                && explosion.world.getBlockState(below).isSolidRender(explosion.world, below)
                && explosion.world.random.nextInt(3) == 0) {
            explosion.world.setBlockAndUpdate(pos, NuclearSpecialContent.BALEFIRE.get().defaultBlockState());
        }
    }
}
