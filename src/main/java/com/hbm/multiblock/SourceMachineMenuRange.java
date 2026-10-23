package com.hbm.multiblock;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Default menu validity of X5846 TileEntityMachineBase; real source overrides stay with their owners. */
public final class SourceMachineMenuRange {
    public static final double DEFAULT_RANGE = 12D;

    private SourceMachineMenuRange() {}

    public static boolean isValid(BlockEntity machine, Player player) {
        var level = machine.getLevel();
        var pos = machine.getBlockPos();
        if (level == null || level.getBlockEntity(pos) != machine) return false;
        return player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5)
                <= DEFAULT_RANGE * DEFAULT_RANGE;
    }
}
