package com.hbm.energy;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** The adjacent target position and outward direction, like legacy DirPos. */
public record NodeConnection(BlockPos target, Direction direction) {
    public NodeConnection {
        target = Objects.requireNonNull(target).immutable();
        Objects.requireNonNull(direction);
    }

    public BlockPos origin() {
        return target.relative(direction.getOpposite());
    }
}
