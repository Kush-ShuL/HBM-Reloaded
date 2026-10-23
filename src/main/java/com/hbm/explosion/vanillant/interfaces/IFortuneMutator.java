package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.block.state.BlockState;

public interface IFortuneMutator {

	public int mutateFortune(ExplosionVNT explosion, BlockState block, int x, int y, int z);
}
