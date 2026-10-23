package com.hbm.entity.mob.ufo;
import com.hbm.item.spawn.LegacyChopperSpawnerItem;import net.minecraft.world.entity.Mob;
/** The real ItemChopper UFO branch, reusing the unique common placement/liquid permissions/custom-name/consume implementation. */
public final class UfoSpawnerItem extends LegacyChopperSpawnerItem {public UfoSpawnerItem(){super(UfoContent.UFO,1);}@Override protected double spawnHeightOffset(){return 35;}@Override protected void initialize(Mob entity){((EntityUFO)entity).scanCooldown=100;}}
