package com.hbm.entity.mob.ghost;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.glyphid.ai.LegacyMobController;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;

/** Exact source EntityCreature: no monster aggression/Peaceful removal and no silently activated source-inactive task list. */
public final class EntityGhost extends PathfinderMob implements LegacyTrackingRange {
    private final LegacyMobController.PassiveState sourceAi=new LegacyMobController.PassiveState();
    public EntityGhost(EntityType<? extends EntityGhost> type,Level world){
        super(type,world);moveControl=new MoveControl(this){@Override public void tick(){}};lookControl=new LookControl(this){@Override public void tick(){}};jumpControl=new JumpControl(this){@Override public void tick(){}};
    }
    public EntityGhost(Level world){this(GhostContent.GHOST.get(),world);}
    public static AttributeSupplier.Builder attributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,8).add(Attributes.MOVEMENT_SPEED,.2).add(Attributes.FOLLOW_RANGE,16);}
    @Override public int legacyTrackingRange(){return 1000;}
    @Override protected void registerGoals(){
        // Source constructor adds Swimming/Wander/LookIdle but never overrides
        // EntityLiving.isAIEnabled() (official sw.bk() returns false). Those tasks
        // are inactive; the actual old EntityCreature action-state path runs instead.
    }
    @Override protected void customServerAiStep(){LegacyMobController.passiveCreature(this,sourceAi);}
    @Override public void checkDespawn(){/* The shared original idle branch calls source despawn once, at its original position. */}
    @Override public void setHealth(float ignored){super.setHealth(getMaxHealth());}
    @Override public boolean isInvulnerableTo(DamageSource source){return true;}
    @Override public boolean hurt(DamageSource source,float amount){return false;}
    @Override public void tick(){super.tick();if(!level().isClientSide&&!level().getEntitiesOfClass(Player.class,getBoundingBox().inflate(50,50,50),player->true).isEmpty())discard();}
    @Override public boolean shouldRenderAtSqrDistance(double distance){double range=getBoundingBox().getSize()*64D*10D;return distance<range*range;}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","ghost_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;}
    // No source drops/sound overrides/custom NBT/egg/biome spawn entry exist.
}
