package com.hbm.api.machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
/** Source pressure-failure callback. */
public interface IOverpressurable {void explode(Level level,BlockPos pos);}
