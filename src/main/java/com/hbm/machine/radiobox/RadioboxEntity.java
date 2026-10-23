package com.hbm.machine.radiobox;

import com.hbm.api.machine.MachineView;
import com.hbm.damage.HbmDamageSources;
import com.hbm.energy.*;
import com.hbm.entity.mob.fbi.EntityFBI;
import com.hbm.entity.mob.fbi.EntityFBIDrone;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Actual TileEntityRadiobox: one six-face HE receiver, no slots, broadcast channel or computer. */
public final class RadioboxEntity extends BlockEntity implements EnergyReceiver, EnergyConductor, MachineView, MuffledMachine {
    public static final long MAX_POWER = 500_000L;
    public static final long TICK_COST = 25_000L;
    private long power;
    public boolean infinite, muffled, tilted;
    private PowerNode node;
    private boolean loaded = true;

    public RadioboxEntity(BlockPos pos, BlockState state) { super(RadioboxContent.ENTITY.get(), pos, state); }

    public static void tick(Level level, BlockPos pos, BlockState state, RadioboxEntity box) {
        if (!(level instanceof ServerLevel server)) return;
        if (box.node == null || box.node.isExpired()) {
            box.node = new PowerNode(pos);
            HbmEnergy.nodespace(server).createNode(box.node);
        }
        if (box.node.hasValidNet()) box.node.addReceiver(box);
        if (state.getValue(RadioboxBlock.META) <= 5 || box.power < TICK_COST && !box.infinite) return;
        if (!box.infinite) { box.power -= TICK_COST; box.setChanged(); }

        // The source range is the integer anchor +/-15, not a sphere or a box centered at +.5.
        // Enemy is the native successor of the original IMob marker, not every Mob or living entity.
        var area = new AABB(pos.getX() - 15, pos.getY() - 15, pos.getZ() - 15,
                            pos.getX() + 15, pos.getY() + 15, pos.getZ() + 15);
        for (var entity : server.getEntitiesOfClass(Entity.class, area, target -> target instanceof Enemy)) {
            if (entity instanceof EntityFBI || entity instanceof EntityFBIDrone) continue;
            // Keep actual damage callbacks, immunity and ordinary hurt resistance; never discard targets.
            entity.hurt(HbmDamageSources.source(server, HbmDamageSources.enervation), 20F);
        }
    }

    @Override public long getPower() { return power; }
    @Override public void setPower(long amount) { power = amount; }
    @Override public long getMaxPower() { return MAX_POWER; }
    @Override public PowerNode energyNode() { return node; }
    @Override public boolean canConnect(Direction side) { return side != null; }
    @Override public boolean isLoaded() { return loaded && !isRemoved() && level != null; }
    @Override public long energyStored() { return power; }
    @Override public long energyCapacity() { return MAX_POWER; }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; }

    private void disconnect() {
        if (node != null && !node.isExpired() && level instanceof ServerLevel server)
            HbmEnergy.nodespace(server).destroyNode(node);
        node = null;
    }
    @Override public void onLoad() { super.onLoad(); loaded = true; }
    @Override public void onChunkUnloaded() { loaded = false; disconnect(); super.onChunkUnloaded(); }
    @Override public void setRemoved() { loaded = false; disconnect(); super.setRemoved(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putLong("power", power); tag.putBoolean("infinite", infinite);
        tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        power = tag.getLong("power"); infinite = tag.getBoolean("infinite");
        muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
    }
}
