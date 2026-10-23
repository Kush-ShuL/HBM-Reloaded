package com.hbm.explosion.vanillant.interfaces;

import java.util.HashSet;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockAllocator {

	public HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size);
}
