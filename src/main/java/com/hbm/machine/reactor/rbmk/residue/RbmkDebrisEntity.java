package com.hbm.machine.reactor.rbmk.residue;

import com.hbm.exposure.ExposureEffects;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.registry.HbmContent;
import java.util.ArrayList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Actual EntityRBMKDebris and its original Y/X/Z collision path; not vanilla item physics. */
public final class RbmkDebrisEntity extends Entity implements com.hbm.entity.LegacyTrackingRange {
    public enum Type { BLANK, ELEMENT, FUEL, ROD, GRAPHITE, LID }
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(RbmkDebrisEntity.class, EntityDataSerializers.INT);
    public float rotation, lastRotation;
    @Override public int legacyTrackingRange() { return 1000; }
    public RbmkDebrisEntity(EntityType<? extends RbmkDebrisEntity> type, Level world) {
        super(type, world);
        random.nextFloat(); // The original parent randomizes its hidden rot; the subclass renderer's shadowed rot remains zero.
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(TYPE, 0); }
    public void setType(Type type) { entityData.set(TYPE, type.ordinal()); refreshDimensions(); }
    public Type debrisType() { return entityData == null ? Type.BLANK : Type.values()[Math.abs(entityData.get(TYPE) % Type.values().length)]; }
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) { super.onSyncedDataUpdated(key); if (TYPE.equals(key)) refreshDimensions(); }
    @Override public EntityDimensions getDimensions(Pose pose) {
        return switch (debrisType()) {
            case BLANK -> EntityDimensions.fixed(.5F, .5F);
            case ELEMENT -> EntityDimensions.fixed(1, 1);
            case FUEL, GRAPHITE -> EntityDimensions.fixed(.25F, .25F);
            case LID -> EntityDimensions.fixed(1, .5F);
            case ROD -> EntityDimensions.fixed(.75F, .5F);
        };
    }
    @Override public boolean canBeCollidedWith() { return true; }
    @Override public boolean isPickable() { return true; }
    @Override public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide) {
            var salvage = switch (debrisType()) {
                case BLANK, ELEMENT, ROD -> new ItemStack(HbmContent.item("debris_metal"));
                case FUEL -> new ItemStack(HbmContent.item("debris_fuel"));
                case GRAPHITE -> new ItemStack(HbmContent.item("debris_graphite"));
                case LID -> new ItemStack(RbmkResidueContent.LID.get());
            };
            if (player.getInventory().add(salvage)) discard();
            player.inventoryMenu.broadcastChanges();
        }
        return InteractionResult.PASS;
    }
    @Override public void tick() {
        tickCount++;
        if (level() instanceof ServerLevel world) {
            if (debrisType() == Type.LID && getDeltaMovement().y > 0) {
                var hit = world.clip(new ClipContext(position(), position().add(getDeltaMovement().scale(2)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hit.getType() == HitResult.Type.BLOCK) {
                    var pos = hit.getBlockPos();
                    for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
                        int distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
                        if (distance <= 1 || random.nextInt(distance) == 0) world.removeBlock(pos.offset(x, y, z), false);
                    }
                    discard();
                }
            }
            if (debrisType() == Type.FUEL || debrisType() == Type.GRAPHITE)
                for (var living : world.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(2.5)))
                    living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION, 1200, debrisType() == Type.FUEL ? 9 : 4));
            if (!RbmkDials.getPermaScrap(world) && tickCount > lifetime() + getId() % 50) discard();
        }
        xo = getX(); yo = getY(); zo = getZ();
        setDeltaMovement(getDeltaMovement().add(0, -.04, 0));
        moveLegacy(getDeltaMovement());
        lastRotation = rotation;
        if (onGround()) setDeltaMovement(getDeltaMovement().multiply(.85, -.5, .85));
        else { rotation += 10; if (rotation >= 360) { rotation -= 360; lastRotation -= 360; } }
    }
    private int lifetime() { return switch (debrisType()) { case BLANK, ELEMENT -> 3600; case FUEL -> 12000; case GRAPHITE -> 18000; case LID -> 600; case ROD -> 1200; }; }
    private void moveLegacy(Vec3 movement) {
        var box = getBoundingBox(); var shapes = new ArrayList<VoxelShape>();
        level().getBlockCollisions(this, box.expandTowards(movement)).forEach(shapes::add);
        shapes.addAll(level().getEntityCollisions(this, box.expandTowards(movement)));
        double dy = Shapes.collide(Direction.Axis.Y, box, shapes, movement.y); box = box.move(0, dy, 0);
        double dx = Shapes.collide(Direction.Axis.X, box, shapes, movement.x); box = box.move(dx, 0, 0);
        double dz = Shapes.collide(Direction.Axis.Z, box, shapes, movement.z); box = box.move(0, 0, dz);
        setBoundingBox(box); setPosRaw((box.minX + box.maxX) / 2, box.minY, (box.minZ + box.maxZ) / 2);
        horizontalCollision = movement.x != dx || movement.z != dz; verticalCollision = movement.y != dy;
        setOnGround(movement.y != dy && movement.y < 0);
        setDeltaMovement(movement.x != dx ? movement.x * -.75 : movement.x, movement.y != dy ? 0 : movement.y, movement.z != dz ? movement.z * -.75 : movement.z);
        checkInsideBlocks();
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { entityData.set(TYPE, tag.getInt("debtype")); refreshDimensions(); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("debtype", entityData.get(TYPE)); }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return distance < 16384; }
}
