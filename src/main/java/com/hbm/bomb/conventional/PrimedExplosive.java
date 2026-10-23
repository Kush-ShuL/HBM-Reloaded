package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** EntityTNTPrimedBase, including the old post-decrement fuse and exact motion constants. */
public final class PrimedExplosive extends Entity implements TraceableEntity, com.hbm.entity.LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> BOMB = SynchedEntityData.defineId(PrimedExplosive.class, EntityDataSerializers.INT);
    private int fuse = 80; // Source does not synchronize shortened chain-reaction fuses to the client.
    public boolean detonateOnCollision;
    private LivingEntity placedBy;
    public PrimedExplosive(EntityType<? extends PrimedExplosive> type, Level level) { super(type, level); blocksBuilding = true; }
    public PrimedExplosive(Level level, double x, double y, double z, LivingEntity igniter, Block block) {
        this(ConventionalContent.PRIMED.get(), level);
        setPos(x, y - getBbHeight() / 2F, z);
        float angle = (float) (Math.random() * Math.PI * 2D);
        setDeltaMovement((double) (-((float) Math.sin(angle)) * .02F), .2D, (double) (-((float) Math.cos(angle)) * .02F));
        xo = getX(); yo = getY(); zo = getZ(); placedBy = igniter;
        entityData.set(BOMB, BuiltInRegistries.BLOCK.getId(block));
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(BOMB, 0); }
    @Override protected MovementEmission getMovementEmission() { return MovementEmission.NONE; }
    @Override public boolean isPickable() { return !isRemoved(); }
    @Override public int legacyTrackingRange() { return 1000; }
    public double legacyY() { return getY() + getBbHeight() / 2F; }
    public int fuse() { return fuse; }
    public void setFuse(int fuse) { this.fuse = fuse; }
    public Block bombBlock() { return BuiltInRegistries.BLOCK.byId(entityData.get(BOMB)); }
    @Override public LivingEntity getOwner() { return placedBy; }
    public LivingEntity getTntPlacedBy() { return placedBy; }
    @Override public void tick() {
        xo = getX(); yo = getY(); zo = getZ();
        setDeltaMovement(getDeltaMovement().add(0, -.04D, 0));
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(.98D));
        if (onGround()) setDeltaMovement(getDeltaMovement().multiply(.7D, -.5D, .7D));
        int before = fuse(); setFuse(before - 1);
        if (before <= 0 || detonateOnCollision && (horizontalCollision || verticalCollision)) {
            discard();
            if (!level().isClientSide && bombBlock() instanceof IFuckingExplode bomb) bomb.explodeEntity(level(), getX(), legacyY(), getZ(), this);
        } else level().addParticle(ParticleTypes.SMOKE, getX(), legacyY() + .5D, getZ(), 0, 0, 0);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Fuse", (byte) fuse());
        // Numeric block ids are a session registry detail in 1.21; persist its stable equivalent.
        tag.putString("Tile", BuiltInRegistries.BLOCK.getKey(bombBlock()).toString());
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        setFuse(tag.getByte("Fuse"));
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Tile"));
        Block block = id == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(id);
        entityData.set(BOMB, BuiltInRegistries.BLOCK.getId(block));
        // The original deliberately does not save collision ignition or placer.
        detonateOnCollision = false; placedBy = null;
    }
}
