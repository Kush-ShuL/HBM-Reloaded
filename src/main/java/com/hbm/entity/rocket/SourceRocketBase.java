package com.hbm.entity.rocket;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.satellite.SatelliteFrequency;
import com.hbm.satellite.SatelliteRegistry;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** X5846 EntityRocketBase; the original ascending carrier, not a missile trajectory. */
public abstract class SourceRocketBase extends Entity implements LegacyTrackingRange {
    protected final ItemStack[] payload;
    protected double acceleration;
    protected SourceRocketBase(EntityType<?> type, Level level, int capacity) {
        super(type, level); noCulling = true; payload = new ItemStack[capacity]; Arrays.fill(payload, ItemStack.EMPTY);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override public void tick() {
        // Original deliberately does not call Entity.onUpdate: no invented gravity, collision or drag.
        var motion = getDeltaMovement();
        if (motion.y < 2) { acceleration += .00025D; motion = motion.add(0, acceleration, 0); setDeltaMovement(motion); }
        xo = xOld = getX(); yo = yOld = getY(); zo = zOld = getZ();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        if (getY() > 600) { deployPayload(); discard(); }
    }
    public void setSat(ItemStack stack) { payload[0] = stack; }
    public void setPayload(List<ItemStack> contents) { for (int i = 0; i < contents.size(); i++) payload[i] = contents.get(i); }
    protected void deployPayload() {
        if (level() instanceof ServerLevel server && !payload[0].isEmpty() && payload[0].getItem() instanceof SatelliteFrequency)
            SatelliteRegistry.orbit(server, payload[0], SatelliteFrequency.getFreqS(payload[0]), getX(), getY(), getZ());
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        Arrays.fill(payload, ItemStack.EMPTY);
        for (var value : tag.getList("items", Tag.TAG_COMPOUND)) { var entry = (CompoundTag) value; int slot = entry.getByte("slot"); if (slot >= 0 && slot < payload.length) payload[slot] = ItemStack.parseOptional(registryAccess(), entry); }
        // Source acceleration is transient and restarts at zero on reload. Entity already persists Motion.
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        var list = new ListTag(); for (int i = 0; i < payload.length; i++) if (!payload[i].isEmpty()) { var entry = (CompoundTag) payload[i].save(registryAccess()); entry.putByte("slot", (byte) i); list.add(entry); } tag.put("items", list);
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 500000; }
    @Override public int legacyTrackingRange() { return 1000; }
}
