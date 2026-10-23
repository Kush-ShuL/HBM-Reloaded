package com.hbm.entity.rocket;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.damage.HbmDamageSources;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherContent;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import com.hbm.satellite.*;
import com.hbm.world.soyuz.EntitySoyuzCapsule;
import java.util.function.Consumer;
import net.minecraft.network.syncher.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Source18-slot carrier: satellite deployment or the existing, actual Soyuz cargo descent. */
public final class EntityRocketSoyuz extends SourceRocketBase {
    private static final EntityDataAccessor<Integer> SKIN = SynchedEntityData.defineId(EntityRocketSoyuz.class, EntityDataSerializers.INT);
    public static Consumer<EntityRocketSoyuz> CLIENT_TICK = rocket -> {};
    public int mode, targetX, targetZ;
    private boolean memed;
    public EntityRocketSoyuz(EntityType<?> type, Level level) { super(type, level, 18); }
    public EntityRocketSoyuz(Level level) { this(SoyuzLauncherContent.ROCKET.get(), level); }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(SKIN, 0); }
    public void setSkin(int skin) { entityData.set(SKIN, skin); }
    public int getSkin() { return entityData.get(SKIN); }
    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) {
            for (var entity : level().getEntities(this, new AABB(getX() - 5, getY() - 15, getZ() - 5, getX() + 5, getY(), getZ() + 5))) {
                entity.igniteForSeconds(15); entity.hurt(HbmDamageSources.source(level(), HbmDamageSources.exhaust), 100);
                if (entity instanceof ServerPlayer player) {
                    if (!memed) { memed = true; level().playSound(null, getX(), getY(), getZ(), HbmSounds.get("alarm.soyuzed"), SoundSource.BLOCKS, 100, 1); }
                    HbmAdvancements.trigger(player, "achSoyuz");
                }
            }
        } else CLIENT_TICK.accept(this);
    }
    @Override protected void deployPayload() {
        if (!(level() instanceof ServerLevel server)) return;
        if (mode == 0 && !payload[0].isEmpty()) {
            var load = payload[0];
            if (load.is(HbmContent.item("flame_pony"))) { ExplosionLarge.spawnTracers(level(), getX(), getY(), getZ(), 25); HbmAdvancements.allInDimension(level(), "achSpace"); }
            if (load.getItem() instanceof SatelliteFrequency) SatelliteRegistry.orbit(server, load, SatelliteFrequency.getFreqS(load), getX(), getY(), getZ());
        }
        if (mode == 1) {
            var capsule = new EntitySoyuzCapsule(level()); capsule.payload = payload; capsule.soyuz = getSkin(); capsule.setPos(targetX + .5, 600, targetZ + .5);
            // Same actual destination chunk load before spawning; no space dimension or substitute landing entity.
            server.getChunk(targetX >> 4, targetZ >> 4); server.addFreshEntity(capsule);
        }
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); setSkin(tag.getInt("skin")); targetX = tag.getInt("targetX"); targetZ = tag.getInt("targetZ"); mode = tag.getInt("mode"); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putInt("skin", getSkin()); tag.putInt("targetX", targetX); tag.putInt("targetZ", targetZ); tag.putInt("mode", mode); }
}
