package com.hbm.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Narrow, lossless coordinate adapter for the original Forge cellular algorithms. */
public final class LegacyFluidWorld {
    public final BlockGetter access;
    public LegacyFluidWorld(BlockGetter access) { this.access = access; }
    public Level level() { return (Level) access; }
    public BlockState state(int x, int y, int z) { return access.getBlockState(new BlockPos(x, y, z)); }
    public Block getBlock(int x, int y, int z) { return state(x, y, z).getBlock(); }
    public boolean isAir(int x, int y, int z) { var block=state(x,y,z);return block.isAir()||block.getBlock() instanceof com.hbm.block.light.SourceBeamBlock; }
    public LegacyMaterial material(int x, int y, int z) { return LegacyMaterials.of(state(x, y, z)); }
    public int getBlockMetadata(int x, int y, int z) {
        BlockState state = state(x, y, z);
        if (state.hasProperty(LegacyFluidBlock.LEVEL)) return state.getValue(LegacyFluidBlock.LEVEL);
        if (state.hasProperty(com.hbm.block.ore.LegacyOreBlock.LEGACY_META)) return state.getValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META);
        return 0;
    }
    public void setBlock(int x, int y, int z, Block block) { setBlock(x, y, z, block, 0, 3); }
    public void setBlock(int x, int y, int z, Block block, int metadata, int flags) {
        if (y < access.getMinBuildHeight() || y >= access.getMaxBuildHeight()) return;
        BlockState state = block.defaultBlockState();
        if (state.hasProperty(LegacyFluidBlock.LEVEL)) state = state.setValue(LegacyFluidBlock.LEVEL, metadata & 15);
        else if (state.hasProperty(com.hbm.block.ore.LegacyOreBlock.LEGACY_META)) state = state.setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META, metadata & 15);
        level().setBlock(new BlockPos(x, y, z), state, flags);
    }
    public void setBlockMetadataWithNotify(int x, int y, int z, int metadata, int flags) {
        // The old setter changes metadata on the current block, even after it changed during a callback.
        setBlock(x, y, z, getBlock(x, y, z), metadata, flags);
    }
    public void scheduleBlockUpdate(int x, int y, int z, Block block, int delay) { level().scheduleTick(new BlockPos(x, y, z), block, delay); }
    public void notifyBlocksOfNeighborChange(int x, int y, int z, Block block) { level().updateNeighborsAt(new BlockPos(x, y, z), block); }
    public int getHeight() { return access.getMaxBuildHeight(); }
    public int getMinHeight() { return access.getMinBuildHeight(); }
    public void drop(int x, int y, int z) { Block.dropResources(state(x, y, z), level(), new BlockPos(x, y, z)); }
}
