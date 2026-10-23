package com.hbm.network.cargo;
import net.minecraft.core.BlockPos;
/** Actual IDroneLinkable source contract; route coordinates belong to each real waypoint/crate. */
public interface DroneLinkable {BlockPos getPoint();void setNextTarget(int x,int y,int z);}
