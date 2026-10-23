package com.hbm.entity.mob.glyphid.hive;

import com.hbm.block.LegacyBlockTraits;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.entity.mob.glyphid.ai.LegacyMobController;
import com.hbm.entity.mob.glyphid.special.EntityGlyphidNuclear;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.world.glyphidhive.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;

/** Actual ninth Glyphid; source task quirks are retained rather than replacing its expansion AI. */
public final class EntityGlyphidScout extends EntityGlyphid {
    boolean hasTarget;
    int timer,scoutingRange=45,minDistanceToHive=8;
    boolean useLargeHive;
    float largeHiveChance=GlyphidConfig.LARGE_HIVE_CHANCE.get();
    public EntityGlyphidScout(EntityType<? extends EntityGlyphidScout> type,Level level){super(type,level);}
    public EntityGlyphidScout(Level level){this(GlyphidHiveContent.SCOUT.get(),level);}
    @Override public boolean isScout(){return true;}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_scout.png");}
    @Override public double sourceScale(){return .75D;}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getScout();}
    @Override public boolean doHurtTarget(Entity victim){if(super.doHurtTarget(victim)&&victim instanceof LivingEntity living){living.addEffect(new MobEffectInstance(MobEffects.POISON,10*20,3));return true;}return false;}
    @Override public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount,2),100);}
    @Override public void tick(){
        super.tick();
        if(legacyTarget!=null&&tickCount%60==0)legacyTarget=findLegacyTarget();
        // Source OR is intentional: it is always true when taskWaypoint is null.
        if((getCurrentTask()!=TASK_BUILD_HIVE||getCurrentTask()!=TASK_TERRAFORM)&&taskWaypoint==null){
            if(GlyphidConfig.guidance()&&GlyphidHiveEvents.targetCoords!=null){
                if(!hasTarget){var direction=playerBaseDirFinder(position(),getPlayerTargetDirection());var target=new EntityWaypoint(level());target.moveTo(direction.x,direction.y,direction.z,0,0);target.maxAge=300;target.radius=6;target.setWaypointType(TASK_BUILD_HIVE);level().addFreshEntity(target);hasTarget=true;setCurrentTask(TASK_RETREAT_FOR_REINFORCEMENTS,target);}
                if(super.isAtDestination()){setCurrentTask(TASK_BUILD_HIVE,null);hasTarget=false;}
            }else setCurrentTask(TASK_BUILD_HIVE,null);
        }
        if(getCurrentTask()==TASK_BUILD_HIVE||getCurrentTask()==TASK_TERRAFORM){
            if(!level().isClientSide&&!hasTarget){if(scoutingRange!=60&&hasNuclearGlyphidNearby())setCurrentTask(TASK_TERRAFORM,null);if(expandHive()){addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,180*20,1));hasTarget=true;}}
            if(taskWaypoint==null&&hasTarget)hasTarget=false;
            if(getCurrentTask()==TASK_TERRAFORM&&super.isAtDestination()&&canBuildHiveHere())communicate(TASK_TERRAFORM,taskWaypoint);
            if(tickCount%10==0&&isAtDestination()){
                timer++;
                if(!level().isClientSide&&canBuildHiveHere()){
                    if(timer==1){var additional=new EntityWaypoint(level());additional.moveTo(getX(),getY(),getZ(),0,0);additional.setWaypointType(TASK_IDLE);var home=new EntityWaypoint(level());home.setWaypointType(TASK_RETREAT_FOR_REINFORCEMENTS);home.setAdditionalWaypoint(additional);home.moveTo(homeX,homeY,homeZ,0,0);home.maxAge=1200;home.radius=6;level().addFreshEntity(home);taskWaypoint=home;addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40*20,10));communicate(TASK_RETREAT_FOR_REINFORCEMENTS,taskWaypoint);}
                    else if(timer>=5){new ExplosionVNT(level(),getX(),getY(),getZ(),5,this).setEntityProcessor(new EntityProcessorStandard()).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectStandard()).explode();GlyphidHivePlacement.generateSmall((net.minecraft.server.level.ServerLevel)level(),(int)Math.floor(getX()),(int)Math.floor(getY()),(int)Math.floor(getZ()),new com.hbm.world.structure.generated.SourceRandom(random),subtype()!=TYPE_NORMAL,false);discard();}
                    else communicate(TASK_FOLLOW,taskWaypoint);
                }
            }
        }
    }
    public boolean canBuildHiveHere(){int length=useLargeHive?16:8;for(int i=0;i<8;i++){float angle=(float)Math.toRadians(360D/16*i);var delta=com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(0,0,length);delta.rotateAroundY(angle);var start=position().add(0,1,0);var hit=level().clip(new net.minecraft.world.level.ClipContext(start,start.add(delta.xCoord,0,delta.zCoord),net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,this));if(hit instanceof BlockHitResult block&&hit.getType()==HitResult.Type.BLOCK&&level().getBlockState(block.getBlockPos()).is(GlyphidHiveMaterialContent.BASE.get())){setCurrentTask(TASK_IDLE,null);hasTarget=false;return false;}}return true;}
    @Override public boolean isAtDestination(){return getCurrentTask()==TASK_BUILD_HIVE&&super.isAtDestination();}
    public boolean hasNuclearGlyphidNearby(){for(var entity:level().getEntities(this,new AABB(getX()-8,getY()-8,getZ()-8,getX()+8,getY()+8,getZ()+8)))if(entity instanceof EntityGlyphidNuclear)return true;return false;}
    @Override public boolean expandHive(){int nestX=random.nextInt((homeX+scoutingRange)-(homeX-scoutingRange))+(homeX-scoutingRange),nestZ=random.nextInt((homeZ+scoutingRange)-(homeZ-scoutingRange))+(homeZ-scoutingRange);int nestY=level().getHeight(Heightmap.Types.WORLD_SURFACE,nestX,nestZ);var pos=new BlockPos(nestX,nestY-1,nestZ);var state=level().getBlockState(pos);boolean distance=com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(nestX-homeX,nestY-homeY,nestZ-homeZ).lengthVector()>minDistanceToHive;
        if(distance&&!LegacyMaterials.of(state).name().equals("air")&&LegacyBlockTraits.isNormalCube(level(),pos,state)&&!state.is(GlyphidHiveMaterialContent.BASE.get())){
            if(BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(ResourceLocation.parse("hbm:basalt"))){useLargeHive=true;largeHiveChance/=2;addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,60*20,3));}
            if(!level().isClientSide){var nest=new EntityWaypoint(level());nest.setWaypointType(getCurrentTask());nest.radius=5;if(useLargeHive)nest.setHighPriority();nest.moveTo(nestX,nestY,nestZ,0,0);level().addFreshEntity(nest);taskWaypoint=nest;setCurrentTask(getCurrentTask(),taskWaypoint);communicate(TASK_BUILD_HIVE,taskWaypoint);}return true;
        }return false;
    }
    @Override public void carryOutTask(){if(!level().isClientSide&&taskWaypoint==null)switch(getCurrentTask()){
        case TASK_INITIATE_RETREAT->{removeEffect(MobEffects.MOVEMENT_SLOWDOWN);addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20*20,4));var additional=new EntityWaypoint(level());additional.moveTo(getX(),getY(),getZ(),0,0);additional.setWaypointType(0);var home=new EntityWaypoint(level());home.setWaypointType(2);home.setAdditionalWaypoint(additional);home.setHighPriority();home.radius=6;home.moveTo(homeX,homeY,homeZ,0,0);level().addFreshEntity(home);communicate(4,home);}
        case TASK_TERRAFORM->{scoutingRange=60;minDistanceToHive=20;}
    }super.carryOutTask();}
    @Override public boolean useExtendedTargeting(){return false;}
    @Override public Entity findLegacyTarget(){return hasEffect(MobEffects.BLINDNESS)?null:LegacyMobController.nearestPlayer(this,10,true);}
    /** Official1.7 Vec3.subtract reverses its operands: current.subtract(target) was target-current. */
    public static Vec3 playerBaseDirFinder(Vec3 current,Vec3 target){var delta=com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(target.x-current.x,target.y-current.y,target.z-current.z).normalize();return current.add(delta.xCoord*10,delta.yCoord*10,delta.zCoord*10);}
    protected Vec3 getPlayerTargetDirection(){var player=LegacyMobController.nearestPlayer(this,300,false);return player!=null?player.position():GlyphidHiveEvents.targetCoords;}
    /** Source no-RNG light test is only used by the independent natural Scout event, not the hive spawn test. */
    public boolean isValidLightLevel(){return level().getMaxLocalRawBrightness(new BlockPos((int)Math.floor(getX()),(int)Math.floor(getBoundingBox().minY),(int)Math.floor(getZ())),level().isThundering()?10:level().getSkyDarken())<=7;}
}
