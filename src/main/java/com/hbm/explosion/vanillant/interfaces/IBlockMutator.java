package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.block.state.BlockState;

public interface IBlockMutator {

	public void mutatePre(ExplosionVNT explosion, BlockState block, int meta, int x, int y, int z);
	public void mutatePost(ExplosionVNT explosion, int x, int y, int z);
}
