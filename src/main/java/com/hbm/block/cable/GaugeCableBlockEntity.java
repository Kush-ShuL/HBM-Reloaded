package com.hbm.block.cable;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Actual network throughput, not a fabricated energy buffer. */
public final class GaugeCableBlockEntity extends NativeCableBlockEntity implements ComputerControl {
    private long deltaTick;
    private long deltaSecond;
    private long deltaLastSecond;
    public GaugeCableBlockEntity(BlockPos pos, BlockState state) { super(CableContent.GAUGE_ENTITY.get(), pos, state); }
    public long deltaTick() { return deltaTick; }
    public long deltaLastSecond() { return deltaLastSecond; }
    public static void tick(Level level, BlockPos pos, BlockState state, GaugeCableBlockEntity gauge) {
        NativeCableBlockEntity.tick(level, pos, state, gauge);
        long oldTick = gauge.deltaTick, oldSecond = gauge.deltaLastSecond;
        if (gauge.node != null && gauge.node.hasValidNet()) {
            gauge.deltaTick = gauge.node.network().getEnergyTracker();
            if (level.getGameTime() % 20 == 0) { gauge.deltaLastSecond = gauge.deltaSecond; gauge.deltaSecond = 0; }
            gauge.deltaSecond = gauge.deltaTick > Long.MAX_VALUE - gauge.deltaSecond ? Long.MAX_VALUE : gauge.deltaSecond + gauge.deltaTick;
        }
        if (oldTick != gauge.deltaTick || oldSecond != gauge.deltaLastSecond || level.getGameTime() % 20 == 0)
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
    @Override public String computerType() { return "ntm_power_gauge"; }
    @Override public String[] computerMethods() { return new String[]{"getTransfer", "getInfo"}; }
    @Override public Object[] callComputer(String method, ComputerArguments arguments) throws ComputerException {
        return switch (method) {
            case "getTransfer" -> new Object[]{deltaTick, deltaLastSecond};
            case "getInfo" -> new Object[]{deltaTick, deltaLastSecond, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()};
            default -> throw new ComputerException("No such method: " + method);
        };
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag = super.getUpdateTag(lookup);
        tag.putLong("deltaTick", deltaTick); tag.putLong("deltaLastSecond", deltaLastSecond);
        return tag;
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        // Source gauge counters are transient; these keys are only present in client sync packets.
        deltaTick = Math.max(0, tag.getLong("deltaTick"));
        deltaLastSecond = Math.max(0, tag.getLong("deltaLastSecond"));
    }
}
