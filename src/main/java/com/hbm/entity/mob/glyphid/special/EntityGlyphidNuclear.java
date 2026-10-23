package com.hbm.entity.mob.glyphid.special;

import com.hbm.LegacyStartup;
import com.hbm.bomb.nuclear.NuclearMukeEffects;
import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.GlyphidStats;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockMutatorDebris;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.item.defuser.NuclearGlyphidDefusable;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Actual Big Man Johnson: source task protocol, defusable hundred-tick death and VNT/Muke. */
public final class EntityGlyphidNuclear extends EntityGlyphid implements NuclearGlyphidDefusable {
    public int deathTicks;
    // Source declares a second hasWaypoint field which shadows the live task flag. Keep them separate.
    private boolean deathWaypointSent;

    public EntityGlyphidNuclear(EntityType<? extends EntityGlyphidNuclear> type, Level level) { super(type, level); }
    public EntityGlyphidNuclear(Level level) { this(GlyphidSpecialContent.NUCLEAR.get(), level); }
    @Override public ResourceLocation getSkin() { return ResourceLocation.parse("hbm:textures/entity/glyphid_nuclear.png"); }
    @Override public double sourceScale() { return 2D; }
    @Override public GlyphidStats.StatBundle getStats() { return GlyphidStats.getStats().getNuclear(); }
    @Override public boolean isNuclearGlyphid() { return true; }
    @Override public int hbm$deathTicks() { return deathTicks; }

    @Override public void tick() {
        super.tick();
        if (tickCount % 20 == 0) {
            if (isAtDestination() && getCurrentTask() == TASK_FOLLOW) setCurrentTask(TASK_IDLE, null);
            if (getCurrentTask() == TASK_BUILD_HIVE && getLastHurtByMob() == null)
                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10 * 20, 3));
            if (getCurrentTask() == TASK_TERRAFORM) setHealth(0);
        }
    }

    /** Source converse rule: only actual Scout instances take this communication. No fake Scout is needed. */
    @Override public void communicate(int task, @Nullable EntityWaypoint waypoint) {
        int radius = waypoint != null ? waypoint.radius : 4;
        for (var entity : level().getEntities(this, new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(radius))) {
            if (entity instanceof EntityGlyphid glyphid && glyphid.isScout() && glyphid.getCurrentTask() != task)
                glyphid.setCurrentTask(task, waypoint);
        }
    }
    @Override public boolean isArmorBroken(float amount) { return random.nextInt(100) <= Math.min(Math.pow(amount * .12, 2), 100); }
    @Override public boolean doesInfectedSpawnMaggots() { return false; }

    /** Source skips generic onDeathUpdate, which was the only old XP drop site. */
    @Override protected void dropExperience(@Nullable Entity attackingEntity) { }

    @Override protected void tickDeath() {
        ++deathTicks;
        if (!deathWaypointSent) { communicate(TASK_INITIATE_RETREAT, null); deathWaypointSent = true; }
        if (deathTicks == 90) {
            for (var entity : level().getEntities(this, new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(8))) {
                if (entity instanceof EntityGlyphid) {
                    // Intentional source quirk: buffs are applied to THIS dying nuclear, once per nearby bug.
                    addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20, 6));
                    addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 15 * 20, 1));
                }
            }
        }
        if (deathTicks == 100) {
            if (!level().isClientSide) {
                var explosion = new ExplosionVNT(level(), getX(), getY(), getZ(), 25, this);
                if (subtype() == TYPE_INFECTED) {
                    spawnMaggots(15 + random.nextInt(6));
                    // Source leaves allocator/processor unset in this branch: entity blast only.
                } else {
                    explosion.setBlockAllocator(new BlockAllocatorStandard(24));
                    explosion.setBlockProcessor(new BlockProcessorStandard()
                            .withBlockEffect(new BlockMutatorDebris(HbmContent.block("volcanic_lava_block").defaultBlockState())).setNoDrop());
                }
                explosion.setEntityProcessor(new EntityProcessorStandard());
                explosion.setPlayerProcessor(new PlayerProcessorStandard());
                explosion.explode();
                level().playSound(null, getX(), getY(), getZ(), HbmSounds.get("weapon.mukeExplosion"), SoundSource.BLOCKS, 15, 1);
                boolean balefire = LegacyStartup.polaroidId == 11 || random.nextInt(100) == 0;
                NuclearMukeEffects.sendAtOrigin(level(), getX(), getY() + .5, getZ(), "muke", balefire, getX(), getY(), getZ());
            }
            discard();
        } else if (!level().isClientSide && deathTicks % 10 == 0) {
            level().playSound(null, getX(), getY(), getZ(), HbmSounds.get("weapon.fstbmbPing"), SoundSource.BLOCKS, 5, 1);
        }
    }
    // Neither deathTicks nor the shadowed death-waypoint flag is saved in the original subclass.
}
