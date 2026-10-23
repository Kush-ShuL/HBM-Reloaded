package com.hbm.entity.aircraft.c130;

import com.hbm.bomb.conventional.ConventionalEffects;
import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.world.chunk.TicketController;

/** X5846 EntityPlaneBase: one forced chunk, source flight, health and network interpolation. */
public abstract class EntityPlaneBase extends Entity implements LegacyTrackingRange {
    public static final TicketController TICKETS = new TicketController(ResourceLocation.parse("hbm:source_aircraft"));
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(EntityPlaneBase.class, EntityDataSerializers.FLOAT);
    protected int turnProgress;
    protected double syncPosX, syncPosY, syncPosZ, syncYaw, syncPitch;
    private Vec3 networkVelocity = Vec3.ZERO;
    private ChunkPos forcedChunk;
    public float health = getMaxHealth();
    public int timer = getLifetime();
    public int trackingRange = 1000;

    protected EntityPlaneBase(EntityType<?> type, Level level) { super(type, level); noCulling = true; }
    public float getMaxHealth() { return 50F; }
    public int getLifetime() { return 200; }
    public float syncedHealth() { return entityData.get(HEALTH); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { builder.define(HEALTH, 50F); }
    @Override public boolean isPickable() { return health > 0; }
    @Override public boolean canBeCollidedWith() { return health > 0; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (source.is(HbmDamageSources.nuclearBlast) || isInvulnerableTo(source)) return false;
        if (!isRemoved() && !level().isClientSide && health > 0) {
            health -= amount;
            if (health <= 0) killPlane();
        }
        return true;
    }
    protected void killPlane() {
        ConventionalEffects.compose(level(), getX(), getY(), getZ(), 25, 3.5F, 2F);
        level().playSound(null, getX(), getY(), getZ(), HbmSounds.get("entity.planeShotDown"), SoundSource.HOSTILE, 25F, 1F);
    }
    @Override public void tick() {
        if (level().isClientSide) health = syncedHealth(); else entityData.set(HEALTH, health);
        xo = xOld = getX(); yo = yOld = getY(); zo = zOld = getZ();
        if (level().isClientSide) {
            if (turnProgress > 0) {
                double x = getX() + (syncPosX - getX()) / turnProgress;
                double y = getY() + (syncPosY - getY()) / turnProgress;
                double z = getZ() + (syncPosZ - getZ()) / turnProgress;
                setYRot((float) (getYRot() + Mth.wrapDegrees(syncYaw - getYRot()) / turnProgress));
                setXRot((float) (getXRot() + (syncPitch - getXRot()) / turnProgress));
                turnProgress--;
                setPos(x, y, z);
            } else setPos(getX(), getY(), getZ());
            return;
        }
        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        rotation();
        if (health <= 0) {
            motion = new Vec3(motion.x, motion.y - .025, motion.z);
            setDeltaMovement(motion);
            for (int i = 0; i < 10; i++) LegacyFluidEffects.gasFlame(level(),
                    getX() + random.nextGaussian() * .5 - motion.x * 2,
                    getY() + random.nextGaussian() * .5 - motion.y * 2,
                    getZ() + random.nextGaussian() * .5 - motion.z * 2, 0, .1, 0);
            // Preserve the source's truncating block lookup and literal zero-height crash limit.
            if (!level().getBlockState(new BlockPos((int) getX(), (int) getY(), (int) getZ())).isAir() || getY() < 0) {
                discard();
                new ExplosionVNT(level(), getX(), getY(), getZ(), 15F).makeStandard().explode();
                level().playSound(null, getX(), getY(), getZ(), HbmSounds.get("entity.planeCrash"), SoundSource.HOSTILE, 25F, 1F);
                return;
            }
        } else setDeltaMovement(motion.x, 0, motion.z);
        if (tickCount > timer) discard();
        loadNeighboringChunks(Mth.floor(getX() / 16D), Mth.floor(getZ() / 16D));
    }
    protected void rotation() {
        Vec3 motion = getDeltaMovement();
        float horizontal = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        setYRot((float) (Math.atan2(motion.x, motion.z) * 180D / Math.PI));
        setXRot((float) (Math.atan2(motion.y, horizontal) * 180D / Math.PI) - 90);
        while (getXRot() - xRotO < -180F) xRotO -= 360F;
        while (getXRot() - xRotO >= 180F) xRotO += 360F;
        while (getYRot() - yRotO < -180F) yRotO -= 360F;
        while (getYRot() - yRotO >= 180F) yRotO += 360F;
    }
    @Override public void lerpMotion(double x, double y, double z) { networkVelocity = new Vec3(x, y, z); setDeltaMovement(networkVelocity); }
    @Override public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        syncPosX = x; syncPosY = y; syncPosZ = z; syncYaw = yaw; syncPitch = pitch;
        turnProgress = steps; setDeltaMovement(networkVelocity);
    }
    @Override public double lerpTargetX() { return turnProgress > 0 ? syncPosX : getX(); }
    @Override public double lerpTargetY() { return turnProgress > 0 ? syncPosY : getY(); }
    @Override public double lerpTargetZ() { return turnProgress > 0 ? syncPosZ : getZ(); }
    @Override public float lerpTargetYRot() { return turnProgress > 0 ? (float) syncYaw : getYRot(); }
    @Override public float lerpTargetXRot() { return turnProgress > 0 ? (float) syncPitch : getXRot(); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        tickCount = tag.getInt("ticksExisted");
        // Original restores only watcher17; the unsaved health field remains the constructor value.
        entityData.set(HEALTH, tag.getFloat("health"));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { tag.putInt("ticksExisted", tickCount); tag.putFloat("health", syncedHealth()); }
    @Override public void onAddedToLevel() {
        super.onAddedToLevel();
        // fac() has already selected the source signal chunk. Loaded entities use their real position.
        if (forcedChunk == null) loadNeighboringChunks(Mth.floor(getX() / 16D), Mth.floor(getZ() / 16D));
    }
    public void loadNeighboringChunks(int x, int z) {
        if (!(level() instanceof ServerLevel server) || isRemoved()) return;
        var next = new ChunkPos(x, z);
        if (next.equals(forcedChunk)) return;
        if (forcedChunk != null) TICKETS.forceChunk(server, this, forcedChunk.x, forcedChunk.z, false, true);
        TICKETS.forceChunk(server, this, x, z, true, true);
        forcedChunk = next;
    }
    public void clearChunkLoader() {
        if (level() instanceof ServerLevel server && forcedChunk != null)
            TICKETS.forceChunk(server, this, forcedChunk.x, forcedChunk.z, false, true);
        forcedChunk = null;
    }
    @Override public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() || reason == RemovalReason.CHANGED_DIMENSION) clearChunkLoader();
        super.remove(reason);
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance) { return true; }
    @Override public int legacyTrackingRange() { return trackingRange; }
}
