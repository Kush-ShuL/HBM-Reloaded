package com.hbm.machine.reactor.rbmk.column;

import com.hbm.api.machine.MachineView;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.core.*;
import com.hbm.machine.reactor.rbmk.residue.*;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Actual TileEntityRBMKBase shared heat/steam state, lids, save/sync and original failure helpers. */
public abstract class RbmkColumnEntity extends BlockEntity implements RbmkColumnState, RbmkLidTarget.Column, MachineView, com.hbm.multiblock.MuffledMachine {
    public double heat;
    private boolean muffledBase;
    public int reasimWater, reasimSteam, craneIndicator;
    public static boolean explodeOnBroken = true;
    private final RbmkHeatTransport transport = new RbmkHeatTransport();
    private CompoundTag lastPacked;
    protected RbmkColumnEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state) && DummyableBlock.isCore(state); }
    public boolean shouldUpdate() { return true; }
    public int trackingRange() { return 15; }
    public double maxHeat() { return 1500; }
    public boolean isLidRemovable() { return true; }
    @Override public boolean hasLid() { return !isLidRemovable() || getBlockState().getValue(DummyableBlock.LEGACY_META) != 12; }
    @Override public boolean isModerated() { return false; }
    @Override public BlockPos corePosition() { return worldPosition; }
    @Override public void installLid(boolean glass) { changeLid(glass ? 13 : 15); }
    public void removeLid() { changeLid(12); }
    private void changeLid(int metadata) {
        if (level == null) return;
        boolean old = explodeOnBroken; explodeOnBroken = false;
        try { level.setBlock(worldPosition, getBlockState().setValue(DummyableBlock.LEGACY_META, metadata), Block.UPDATE_ALL); }
        finally { explodeOnBroken = old; }
        // Native chunk model data needs a rebuild when the original shared core metadata changes its top cover.
        for (int i = 0; i <= 16; i++) { var pos = worldPosition.above(i); var state = level.getBlockState(pos); if (!state.is(getBlockState().getBlock())) break; level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS); }
        setChanged();
    }
    protected void baseTick(ServerLevel world) {
        if (craneIndicator > 0) craneIndicator--;
        transport.tick(world, this);
        sync();
    }
    protected void sync() {
        if (!(level instanceof ServerLevel world)) return;
        var tag = getUpdateTag(world.registryAccess());
        if (tag.equals(lastPacked) && world.getGameTime() % 20 != 0) return;
        lastPacked = tag.copy();
        var packet = ClientboundBlockEntityDataPacket.create(this, (ignored, lookup) -> tag);
        int range = trackingRange();
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(world.getServer(),packet)){for (var player : world.players()) if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < range * range) player.connection.send(packet);dispatchScope.commit();}
    }
    @Override public double passiveCooling(int neighbors) {
        double min = RbmkDials.getPassiveCoolingInner(level), max = RbmkDials.getPassiveCooling(level);
        return min + (max - min) * ((4 - net.minecraft.util.Mth.clamp(neighbors, 0, 4)) / 4D);
    }
    @Override public BlockEntity entity() { return this; }
    @Override public double heat() { return heat; }
    @Override public void heat(double value) { heat = value; }
    @Override public int reasimWater() { return reasimWater; }
    @Override public void reasimWater(int value) { reasimWater = value; }
    @Override public int reasimSteam() { return reasimSteam; }
    @Override public void reasimSteam(int value) { reasimSteam = value; }
    @Override public void changed() { setChanged(); }
    @Override public void setRemoved() { RbmkNeutronField.remove(level, worldPosition); transport.clear(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { RbmkNeutronField.remove(level, worldPosition); transport.clear(); super.onChunkUnloaded(); }
    public abstract String consoleType();
    public CompoundTag consoleData() { return null; }
    public CompoundTag diagnosticData() { var tag = new CompoundTag(); saveState(tag); return tag; }
    protected void saveState(CompoundTag tag) { tag.putBoolean("muffled", isMuffled()); tag.putDouble("heat", heat); tag.putInt("reasimWater", reasimWater); tag.putInt("reasimSteam", reasimSteam); }
    protected void loadState(CompoundTag tag) { setMuffled(tag.getBoolean("muffled")); heat = tag.getDouble("heat"); reasimWater = tag.getInt("reasimWater"); reasimSteam = tag.getInt("reasimSteam"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.saveAdditional(tag, lookup); saveState(tag); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.loadAdditional(tag, lookup); loadState(tag); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag = new CompoundTag(); saveState(tag); tag.putByte("craneIndicator", (byte)craneIndicator); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { loadState(tag); craneIndicator = tag.getByte("craneIndicator"); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    public AABB renderBounds() { return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 17, worldPosition.getZ() + 1); }
    /** Source fallback, not the actual fuel-specific connected meltdown. */
    public void onOverheat() { for (int i = 0; i < 4; i++) level.setBlock(worldPosition.above(i), net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL); }
    public void onMelt(int reduce) {
        int metadata = getBlockState().getValue(DummyableBlock.LEGACY_META);
        standardMelt(reduce);
        if (metadata == 15) spawnDebris(RbmkDebrisEntity.Type.LID);
    }
    protected void standardMelt(int reduce) {
        int height = RbmkDials.getColumnHeight(level); reduce = net.minecraft.util.Mth.clamp(reduce, 1, height);
        if (level.random.nextInt(3) == 0) reduce++;
        for (int i = height; i >= 0; i--) {
            BlockState state;
            if (i <= height + 1 - reduce) state = RbmkResidueContent.RESIDUES.get(reduce > 1 && i == height + 1 - reduce ? RbmkResidueBlock.Kind.BURNING : RbmkResidueBlock.Kind.NORMAL).get().defaultBlockState();
            else state = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            level.setBlock(worldPosition.above(i), state, Block.UPDATE_ALL);
        }
    }
    protected void spawnDebris(RbmkDebrisEntity.Type type) {
        var debris = new RbmkDebrisEntity(RbmkResidueContent.DEBRIS.get(), level); debris.setPos(worldPosition.getX() + .5, worldPosition.getY() + 4, worldPosition.getZ() + .5); debris.setType(type);
        double x = level.random.nextGaussian() * .25, z = level.random.nextGaussian() * .25, y = .25 + level.random.nextDouble() * 1.25;
        if (type == RbmkDebrisEntity.Type.LID) { x *= .5; y += .5; z *= .5; }
        debris.setDeltaMovement(x, y, z); level.addFreshEntity(debris);
    }
    public void meltdown() { RbmkMeltdown.run(this); }
    /** Original boiler.onMelt populates this exact union; non-boiler columns contribute no network. */
    protected void collectOverpressureNetworks(java.util.Set<com.hbm.fluid.network.FluidNetMK2> networks) { }
    @Override public long energyStored() { return 0; }
    @Override public long energyCapacity() { return 0; }
    @Override public List<ProgressView> progressBars() { return List.of(new ProgressView("tile.rbmk.dodd.heat", heat, maxHeat(), 0xE78332), new ProgressView("tile.rbmk.dodd.reasimWater", reasimWater, 16000, 0x487FCC), new ProgressView("tile.rbmk.dodd.reasimSteam", reasimSteam, 16000, 0xD6D6D6)); }

 @Override public boolean isMuffled(){return muffledBase;}
 @Override public void setMuffled(boolean value){muffledBase=value;}
}
