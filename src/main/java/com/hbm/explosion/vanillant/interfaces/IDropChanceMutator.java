package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.level.block.state.BlockState;

public interface IDropChanceMutator {

	public float mutateDropChance(ExplosionVNT explosion, BlockState block, int x, int y, int z, float chance);
}
