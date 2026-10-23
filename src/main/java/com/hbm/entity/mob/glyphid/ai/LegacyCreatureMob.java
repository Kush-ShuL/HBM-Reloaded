package com.hbm.entity.mob.glyphid.ai;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;

/** The real old non-goal EntityCreature/EntityMob controller shared by Glyphids and their actual maggots. */
public abstract class LegacyCreatureMob extends Monster {
    public Entity legacyTarget;public LegacyMobPath legacyPath;public int fleeingTicks,legacyAttackTime,legacyIdleAge;
    private LegacyMobController controller;
    protected LegacyCreatureMob(EntityType<? extends Monster> type,Level world){super(type,world);xpReward=5;moveControl=new MoveControl(this){@Override public void tick(){/* The original controller owns actual strafe/forward. */}};jumpControl=new JumpControl(this){@Override public void tick(){/* The original controller owns the persistent jumping flag. */}};}
    @Override protected void registerGoals(){}
    @Override protected void customServerAiStep(){if(controller==null)controller=new LegacyMobController(this);if(legacyAttackTime>0)legacyAttackTime--;controller.tick();afterLegacyActionState();}
    protected void afterLegacyActionState(){}
    public boolean legacyCeasedAttack(){return false;}
    public boolean legacyMayWander(){return true;}
    public boolean legacyMayDespawn(){return true;}
    public int legacyMaxFall(){return 3;}
    public Entity findLegacyTarget(){return LegacyMobController.nearestPlayer(this,16,true);}
    public float legacyPathWeight(int x,int y,int z){return .5F-level().getLightLevelDependentMagicValue(new net.minecraft.core.BlockPos(x,y,z));}
    public void attackLegacyTarget(Entity target,float distance){if(legacyAttackTime<=0&&distance<2F&&target.getBoundingBox().maxY>getBoundingBox().minY&&target.getBoundingBox().minY<getBoundingBox().maxY){legacyAttackTime=20;doHurtTarget(target);}}
    @Override public boolean hurt(DamageSource source,float amount){boolean result=super.hurt(source,amount);if(result&&source.getEntity()!=this&&source.getEntity()!=getVehicle()&&source.getEntity()!=getFirstPassenger())legacyTarget=source.getEntity();return result;}
    @Override public void checkDespawn(){/* The old controller invokes source despawn only from the old idle action branch. */}
    public void legacyDespawn(){legacyIdleAge=LegacyMobController.despawn(this,legacyIdleAge,this::legacyMayDespawn);}
    @Override public void aiStep(){if(!level().isClientSide&&getLightLevelDependentMagicValue()>0.5F)legacyIdleAge+=2;super.aiStep();}
    @Override public void tick(){super.tick();if(!level().isClientSide&&level().getDifficulty()==Difficulty.PEACEFUL)discard();}
    public float legacyMoveSpeed(){return(float)getAttributeValue(Attributes.MOVEMENT_SPEED);}
    public void legacyClearTarget(){legacyTarget=null;legacyPath=null;}
}
