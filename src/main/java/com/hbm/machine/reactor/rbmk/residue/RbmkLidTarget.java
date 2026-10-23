package com.hbm.machine.reactor.rbmk.residue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Implemented only by the actual RBMKBase blocks: resolves the real column core, never another container. */
public interface RbmkLidTarget {
    Column rbmkCore(Level world, BlockPos clicked);
    interface Column {
        BlockPos corePosition();
        boolean hasLid();
        /** Real core state13(glass)/15(normal), with explodeOnBroken disabled solely around that update. */
        void installLid(boolean glass);
    }
}
