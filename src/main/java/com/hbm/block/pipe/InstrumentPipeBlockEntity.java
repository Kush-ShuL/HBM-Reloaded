package com.hbm.block.pipe;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import com.hbm.fluid.HbmFluids;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

/** Original gauge and counter scheduling, with the same public methods on CC:Tweaked. */
public final class InstrumentPipeBlockEntity extends NativePipeBlockEntity implements ComputerControl {
    private long deltaTick, deltaSecond, deltaLastSecond, counter, lastCounterUpdate = -1;
    public InstrumentPipeBlockEntity(BlockPos pos, BlockState state) { super(pos, state); }
    @Override protected void serverTick(ServerLevel server) {
        super.serverTick(server);
        if (kind() == PipeKind.COUNTER) updateCounter();
        else if (node != null && node.network() != null && type != HbmFluids.NONE) {
            deltaTick = node.network().fluidTracker;
            if (server.getGameTime() % 20 == 0) { deltaLastSecond = deltaSecond; deltaSecond = 0; }
            deltaSecond += deltaTick;
        }
        // Source networkPackNT radius 25, per tick. Trackers are not reconstructed on clients.
        var packet = getUpdatePacket();
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for (var player : server.players()) if (player.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) < 625) player.connection.send(packet);dispatchScope.commit();}
    }
    private boolean pending() { return node != null && node.network() != null && type != HbmFluids.NONE && level != null && lastCounterUpdate != level.getGameTime(); }
    private void updateCounter() { if (pending()) { counter += node.network().fluidTracker; lastCounterUpdate = level.getGameTime(); } }
    public long counter() { return counter; }
    public long currentCounter() { return counter + (pending() ? node.network().fluidTracker : 0); }
    public long deltaTick() { return deltaTick; }
    public long deltaLastSecond() { return deltaLastSecond; }
    @Override public void updateState() { if (!getBlockState().getValue(NativePipeBlock.OPEN)) updateCounter(); super.updateState(); }
    private void setState(int state) {
        if (state != 0 && state != 1) throw new ComputerException("state must be 0 or 1");
        level.setBlock(worldPosition, getBlockState().setValue(NativePipeBlock.OPEN, state == 1), 2);
        level.playSound(null, worldPosition, HbmSounds.get("block.reactorStart"), SoundSource.BLOCKS, 1F, 1F);
        updateState(); setChanged();
    }
    @Override public String computerType() { return kind() == PipeKind.GAUGE ? "ntm_fluid_gauge" : "ntm_fluid_counter_valve"; }
    @Override public String[] computerMethods() { return kind() == PipeKind.GAUGE ? new String[]{"getTransfer", "getFluid", "getInfo"} : new String[]{"getFluid", "getCounter", "resetCounter", "getState", "setState"}; }
    @Override public Object[] callComputer(String method, ComputerArguments arguments) {
        return switch (method) {
            case "getFluid" -> new Object[]{type.getName()};
            case "getTransfer" -> { require(PipeKind.GAUGE); yield new Object[]{deltaTick, deltaLastSecond}; }
            case "getInfo" -> { require(PipeKind.GAUGE); yield new Object[]{deltaTick, deltaLastSecond, type.getName(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()}; }
            case "getCounter" -> { require(PipeKind.COUNTER); yield new Object[]{counter}; }
            case "resetCounter" -> { require(PipeKind.COUNTER); updateCounter(); counter = 0; setChanged(); yield new Object[0]; }
            case "getState" -> { require(PipeKind.COUNTER); yield new Object[]{getBlockState().getValue(NativePipeBlock.OPEN) ? 1 : 0}; }
            case "setState" -> { require(PipeKind.COUNTER); setState(arguments.checkInteger(0)); yield new Object[0]; }
            default -> throw new ComputerException("Unknown method: " + method);
        };
    }
    private void require(PipeKind expected) { if (kind() != expected) throw new ComputerException("Method not available on " + computerType()); }
    /** Hooks for the real ROR dispatcher when that source subsystem is migrated. */
    public String provideRORValue(String name) {
        if (kind() == PipeKind.GAUGE) return switch (name) { case "deltatick" -> Long.toString(deltaTick); case "deltasecond" -> Long.toString(deltaLastSecond); default -> null; };
        return switch (name) { case "value" -> Long.toString(currentCounter()); case "state" -> getBlockState().getValue(NativePipeBlock.OPEN) ? "1" : "0"; default -> null; };
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag, registries); if (kind() == PipeKind.COUNTER) tag.putLong("counter", counter); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries); counter = tag.getLong("counter");
        if (level != null && level.isClientSide) { counter = Math.max(counter, 0); deltaTick = Math.max(tag.getLong("deltaTick"), 0); deltaLastSecond = Math.max(tag.getLong("deltaLastSecond"), 0); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { var tag = super.getUpdateTag(registries); tag.putLong("deltaTick", deltaTick); tag.putLong("deltaLastSecond", deltaLastSecond); return tag; }
}
