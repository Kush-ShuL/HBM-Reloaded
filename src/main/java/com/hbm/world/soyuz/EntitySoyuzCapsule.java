package com.hbm.world.soyuz;

import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Source cargo descent only. The separate unported Soyuz launcher/rocket is not replaced by another rocket. */
public final class EntitySoyuzCapsule extends Entity implements LegacyTrackingRange {
    public int soyuz;
    public ItemStack[] payload = new ItemStack[18];
    public EntitySoyuzCapsule(EntityType<?> type, Level level) { super(type, level); noCulling = true; java.util.Arrays.fill(payload, ItemStack.EMPTY); }
    public EntitySoyuzCapsule(Level level) { this(SoyuzCapsulesContent.DESCENT.get(), level); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { }
    @Override public void tick() {
        var motion = getDeltaMovement();
        if (motion.y > -.2) { motion = motion.add(0, -.02, 0); setDeltaMovement(motion); }
        if (getY() > 600) setPosRaw(getX(), 600, getZ());
        xo = xOld = getX(); yo = yOld = getY(); zo = zOld = getZ();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        var contact = BlockPos.containing(getX(), getY(), getZ());
        if (!level().getBlockState(contact).isAir()) {
            discard();
            if (!level().isClientSide) {
                var landed = BlockPos.containing(getX(), getY() + 1, getZ());
                level().setBlock(landed, SoyuzCapsulesContent.CAPSULE.get().defaultBlockState(), 3);
                if (level().getBlockEntity(landed) instanceof SoyuzCapsuleBlockEntity capsule) {
                    for (int i = 0; i < payload.length; i++) capsule.setItem(i, payload[i] == null ? ItemStack.EMPTY : payload[i]);
                    capsule.setItem(18, SoyuzItem.stack(soyuz));
                }
            }
        }
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("soyuz", soyuz); var list = new ListTag();
        for (int i = 0; i < payload.length; i++) if (payload[i] != null && !payload[i].isEmpty()) {
            var entry = (CompoundTag) payload[i].save(registryAccess()); entry.putByte("slot", (byte) i); list.add(entry);
        }
        tag.put("items", list);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        soyuz = tag.getInt("soyuz");
        for (var value : tag.getList("items", Tag.TAG_COMPOUND)) {
            var entry = (CompoundTag) value; int index = entry.getByte("slot");
            if (index >= 0 && index < payload.length) payload[index] = ItemStack.parseOptional(registryAccess(), entry);
        }
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 500000; }
    @Override public int legacyTrackingRange() { return 1000; }
}
