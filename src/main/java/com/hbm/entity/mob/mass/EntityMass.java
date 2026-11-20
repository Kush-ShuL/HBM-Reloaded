package com.hbm.entity.mob.mass;

import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/** The roaming construct released by the lower red room's pedestal. */
public final class EntityMass extends Monster implements com.hbm.damage.IResistanceProvider {
    private BlockPos home;
    private int moveTimer;
    private Direction direction;

    public EntityMass(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
        setPersistenceRequired();
    }

    @Override protected void registerGoals() {}
    @Override public boolean isPushable() { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && source.getEntity() instanceof Player) discard();
        return false;
    }

    @Override public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server) || !isAlive()) return;
        if (home == null) home = blockPosition();
        if (--moveTimer <= 0) {
            Direction next = Direction.getRandom(random);
            if (direction == null || next != direction.getOpposite()) {
                direction = next;
                moveTimer = 100 + random.nextInt(200);
            }
            if (random.nextInt(3) == 0 && server.getHeight(Heightmap.Types.WORLD_SURFACE, getBlockX(), getBlockZ()) < getY() - 2)
                direction = Direction.DOWN;
        }
        direction = constrainedDirection(server);
        var movement = direction == null ? net.minecraft.world.phys.Vec3.ZERO : net.minecraft.world.phys.Vec3.atLowerCornerOf(direction.getNormal()).scale(.2);
        move(MoverType.SELF, movement);
        int phase = tickCount + getId();
        if (phase % 10 == 0 && server.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
            var center = BlockPos.containing(getX(), getY() + 1.5, getZ());
            for (var pos : BlockPos.betweenClosed(center.offset(-1,-1,-1), center.offset(1,1,1))) {
                if (pos.getY() > server.getMinBuildHeight() && server.hasChunkAt(pos) && !server.isEmptyBlock(pos))
                    server.destroyBlock(pos, false, this);
            }
        }
        for (var target : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox(), e -> e != this && e.isAlive() && !e.isSpectator())) {
            var source=HbmDamageSources.source(server,HbmDamageSources.turbofan);
            com.hbm.damage.LegacyEntityDamage.attackEntityFromNT(target,source,1000F,true,false,0,1000,2);
            if(!target.isAlive())com.hbm.explosion.ExplosionDeathEffects.decide(target,source);
        }
        if (phase % 100 == 0) sound("entity.massScream", 10F, .5F);
        if (random.nextBoolean()) sound("block.assemblerStrike", 1F, .5F + random.nextFloat() * .25F);
        if (random.nextInt(20) == 0) sound("block.crateBreak", 1F, .5F + random.nextFloat() * .25F);
    }

    private Direction constrainedDirection(ServerLevel server) {
        if (getY() < Math.max(server.getMinBuildHeight() + 1, home.getY() - 50)) return Direction.UP;
        if (getY() > Math.min(server.getMaxBuildHeight() - 3, home.getY() + 50)) return Direction.DOWN;
        if (getX() > home.getX() + 50) return Direction.WEST;
        if (getX() < home.getX() - 50) return Direction.EAST;
        if (getZ() > home.getZ() + 50) return Direction.NORTH;
        if (getZ() < home.getZ() - 50) return Direction.SOUTH;
        return direction;
    }

    private void sound(String key, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), HbmSounds.get(key), SoundSource.HOSTILE, volume, pitch);
    }
    @Override public float[] getCurrentDTDR(DamageSource source,float amount,float pierceDT,float pierce){return new float[]{10000F,5F};}
    @Override public void onDamageDealt(DamageSource source,float amount){}

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (home != null) tag.putLong("Home", home.asLong());
        if (direction != null) tag.putByte("Direction", (byte) direction.get3DDataValue());
        tag.putInt("MoveTimer", moveTimer);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Home")) home = BlockPos.of(tag.getLong("Home"));
        if (tag.contains("Direction")) direction = Direction.from3DDataValue(tag.getByte("Direction"));
        moveTimer = tag.getInt("MoveTimer");
    }

    private static final net.neoforged.neoforge.registries.DeferredRegister<EntityType<?>> TYPES =
            net.neoforged.neoforge.registries.DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<EntityMass>> TYPE =
            TYPES.register("entity_mass", () -> EntityType.Builder.of(EntityMass::new, MobCategory.MONSTER)
                    .sized(3, 3).clientTrackingRange(16).updateInterval(3).build("hbm:entity_mass"));
    public static void register(net.neoforged.bus.api.IEventBus bus) {
        TYPES.register(bus);
        bus.addListener((net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) ->
                event.put(TYPE.get(), Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20).build()));
    }
}
