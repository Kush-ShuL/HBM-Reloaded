package com.hbm.missile.custom.launcher;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Complete source TileEntityMultiblock two active branches:3x3base and9x9base+one of four11-high scaffold columns. */
public final class CustomLauncherCoreEntity extends BlockEntity {
    public CustomLauncherCoreEntity(BlockPos pos, BlockState state) { super(CustomLauncherContent.CORE_ENTITY.get(), pos, state); }
    public static void tick(Level level, BlockPos pos, BlockState state, CustomLauncherCoreEntity machine) {
        if (!(level instanceof ServerLevel) || !(state.getBlock() instanceof CustomLauncherCoreBlock core)) return;
        int radius = core.large ? 4 : 1;
        for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
            if (x == 0 && z == 0) continue;
            var target = pos.offset(x, 0, z); if (!level.hasChunkAt(target) || !level.getBlockState(target).is(CustomLauncherContent.STRUCTURE.get())) return;
        }
        if (!core.large) { CustomLauncherContent.COMPACT.get().assemble(level, pos, 0); return; }
        BlockPos[] towers = {pos.offset(3, 0, 0), pos.offset(-3, 0, 0), pos.offset(0, 0, 3), pos.offset(0, 0, -3)};
        for (int orientation = 0; orientation < towers.length; orientation++) {
            boolean present = true;
            for (int y = 1; y < 12; y++) { var target = towers[orientation].above(y); if (!level.hasChunkAt(target) || !level.getBlockState(target).is(CustomLauncherContent.SCAFFOLD.get())) present = false; }
            if (present) { CustomLauncherContent.TABLE.get().assemble(level, pos, orientation); return; }
        }
    }
}
