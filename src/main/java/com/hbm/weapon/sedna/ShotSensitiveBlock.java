package com.hbm.weapon.sedna;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
/** Only source BlockDetonatable blocks with detonateOnShot enabled implement this. */
public interface ShotSensitiveBlock {void onShot(Level level,BlockPos position);}
