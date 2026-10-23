package com.hbm.explosion.vanillant.interfaces;

import java.util.HashMap;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public interface IEntityProcessor {

	public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size);
}
