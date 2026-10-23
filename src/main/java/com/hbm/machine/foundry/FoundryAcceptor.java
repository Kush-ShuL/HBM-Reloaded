package com.hbm.machine.foundry;

import com.hbm.material.MaterialStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/** Source ICrucibleAcceptor: native material quanta, deliberately independent of liquid capabilities. */
public interface FoundryAcceptor {
    boolean canAcceptPartialFlow(Direction side, MaterialStack stack);
    MaterialStack flow(Direction side, MaterialStack stack);
    boolean canAcceptPartialPour(Vec3 hit, Direction side, MaterialStack stack);
    MaterialStack pour(Vec3 hit, Direction side, MaterialStack stack);
}
