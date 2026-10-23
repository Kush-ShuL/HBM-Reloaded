package com.hbm.entity.aircraft.c130;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.world.structure.loot.StructureLootContent;
import com.hbm.world.structure.loot.SupplyCrateBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Original no-collision parachute descent and unbounded source supply-crate contents. */
public final class EntityParachuteCrate extends Entity implements LegacyTrackingRange {
    public final List<ItemStack> items = new ArrayList<>();
    public EntityParachuteCrate(EntityType<?> type, Level level) { super(type, level); noCulling = true; }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override public void tick() {
        xo = xOld = getX(); yo = yOld = getY(); zo = zOld = getZ();
        var motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        if (motion.y > -.2) setDeltaMovement(motion.x, motion.y - .02, motion.z);
        // Source directly changes posY after setPosition, retaining that tick's old bounding box.
        if (getY() > 600) setPosRaw(getX(), 600, getZ());
        var contact = BlockPos.containing(getX(), getY(), getZ());
        if (!level().getBlockState(contact).is(Blocks.AIR)) {
            discard();
            if (!level().isClientSide) {
                var pos = BlockPos.containing(getX(), getY() + 1, getZ());
                level().setBlock(pos, StructureLootContent.SUPPLY.get().defaultBlockState(), 3);
                if (level().getBlockEntity(pos) instanceof SupplyCrateBlockEntity crate) {
                    crate.items.addAll(items);
                    crate.setChanged();
                }
            }
        }
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        items.clear();
        for (var value : tag.getList("items", Tag.TAG_COMPOUND)) items.add(ItemStack.parseOptional(registryAccess(), (CompoundTag) value));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        var list = new ListTag();
        for (var item : items) list.add(item.saveOptional(registryAccess()));
        tag.put("items", list);
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return true; }
    @Override public int legacyTrackingRange() { return 1000; }
}
