package com.hbm.entity.effect;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.conventional.ConventionalProjectiles;
import com.hbm.bomb.conventional.MistExposure;
import com.hbm.damage.HbmDamageSources;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.exposure.HbmExposure;
import com.hbm.fluid.*;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.radiation.HbmRadiation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Original EntityMist's separate temperature, toxin and exposure semantics, rather than EntityChemical's different rules. */
public final class EntityMist extends Entity {
    private static final EntityDataAccessor<Integer> FLUID=SynchedEntityData.defineId(EntityMist.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WIDTH=SynchedEntityData.defineId(EntityMist.class,EntityDataSerializers.FLOAT),HEIGHT=SynchedEntityData.defineId(EntityMist.class,EntityDataSerializers.FLOAT);
    private boolean initialized;public int maxAge=150;
    public EntityMist(EntityType<? extends EntityMist> type,Level level){super(type,level);noPhysics=true;initialized=true;}
    public EntityMist(Level level){this(ConventionalProjectiles.MIST.get(),level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(FLUID,0);builder.define(WIDTH,0F);builder.define(HEIGHT,0F);}
    public EntityMist setArea(float width,float height){entityData.set(WIDTH,width);entityData.set(HEIGHT,height);return this;}
    public EntityMist setDuration(int duration){maxAge=duration;return this;}
    public EntityMist setType(HbmFluidType type){entityData.set(FLUID,type.getID());return this;}
    public HbmFluidType getTypeFluid(){return HbmFluids.fromID(entityData.get(FLUID));}
    public int getMaxAge(){return maxAge;}
    @Override public EntityDimensions getDimensions(Pose pose){return !initialized?EntityDimensions.scalable(.6F,1.8F):EntityDimensions.scalable(entityData.get(WIDTH),entityData.get(HEIGHT));}
    @Override public void tick(){
        // Old setSize preserves the old min corner. Its subsequent setPosition
        // call is suppressed by this entity after age zero; retain that offset.
        float width=entityData.get(WIDTH),height=entityData.get(HEIGHT);if(width!=getBbWidth()||height!=getBbHeight()){var old=getBoundingBox();refreshDimensions();setBoundingBox(new AABB(old.minX,old.minY,old.minZ,old.minX+width,old.minY+height,old.minZ+width));}
        HbmFluidType type=getTypeFluid();
        if(level() instanceof ServerLevel server){
            if(tickCount>=getMaxAge())discard();
            var radiation=type.getTrait(FluidTraits.VentRadiation.class);if(radiation!=null)HbmRadiation.incrementRad(server,Mth.floor(getX()),Mth.floor(getY()),Mth.floor(getZ()),radiation.getRadPerMB()*2);
            double intensity=1D-(double)tickCount/getMaxAge();
            if(type.hasTrait(FluidTraits.Flammable.class)&&isOnFire()){level().explode(this,getX(),getY()+height/2,getZ(),(float)intensity*15,Level.ExplosionInteraction.TNT);discard();return;}
            for(var entity:level().getEntities(this,getBoundingBox().move(-width/2,0,-width/2)))affect(entity,intensity);
        }else for(int i=0;i<2;i++){var box=getBoundingBox();double x=box.minX+(random.nextDouble()-.5)*(box.maxX-box.minX),y=box.minY+random.nextDouble()*(box.maxY-box.minY),z=box.minZ+(random.nextDouble()-.5)*(box.maxZ-box.minZ);LegacyFluidEffects.tower(level(),x,y,z,new LegacyFluidEffects.Tower(.5F,.75F,2F,50+level().random.nextInt(10)).color(type.getColor()));}
    }
    private void affect(Entity entity,double intensity){HbmFluidType type=getTypeFluid();var living=entity instanceof LivingEntity alive?alive:null;
        if(type.temperature>=100){LegacyEntityDamage.attackEntityFromIgnoreIFrame(entity,MistExposure.source(level(),"hbm:mist_hot"),.2F+(type.temperature-100)*.02F);if(type.temperature>=500)entity.igniteForSeconds(10);}
        if(type.temperature< -20&&living!=null){LegacyEntityDamage.attackEntityFromIgnoreIFrame(living,MistExposure.source(level(),"hbm:mist_cold"),.2F+(type.temperature+20)*-.05F);living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,2));living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,100,4));}
        if(type.hasTrait("delicious")&&living!=null&&living.isAlive())living.heal(2F*(float)intensity);
        if(type.hasTrait(FluidTraits.Flammable.class)&&type.hasTrait("liquid")&&living!=null)MistExposure.setOil(living,200);
        if(type.temperature<50&&!type.hasTrait(FluidTraits.Flammable.class))entity.clearFire();
        var corrosive=type.getTrait(FluidTraits.Corrosive.class);if(corrosive!=null&&living!=null){LegacyEntityDamage.attackEntityFromIgnoreIFrame(living,HbmDamageSources.source(level(),HbmDamageSources.acid),corrosive.getRating()/60F);for(int i=0;i<4;i++)MistExposure.damageSuit(living,i,corrosive.getRating()/50);}
        var radiation=type.getTrait(FluidTraits.VentRadiation.class);if(radiation!=null&&living!=null)HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,radiation.getRadPerMB()*5);
        var poison=type.getTrait(FluidTraits.Poison.class);if(poison!=null&&living!=null)living.addEffect(new MobEffectInstance(poison.isWithering()?MobEffects.WITHER:MobEffects.POISON,(int)(100*intensity)));
        var toxin=type.getTrait(FluidTraits.Toxin.class);if(toxin!=null&&living!=null)MistExposure.affectToxin(living,toxin,intensity);
        if(type==HbmFluids.ENDERJUICE&&living!=null)teleportRandomly(living);
        var pheromone=type.getTrait(FluidTraits.Pheromone.class);if(pheromone!=null&&living!=null&&(pheromone.getType()==1&&isGlyphid(living)||pheromone.getType()==2&&living instanceof Player)){int multiplier=pheromone.getType();living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,multiplier*1200,1));living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,multiplier*1200,1));living.addEffect(new MobEffectInstance(MobEffects.REGENERATION,multiplier*40));living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,multiplier*1200));living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,multiplier*1200,1));living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,multiplier*1200));}
    }
    public static boolean isGlyphid(LivingEntity entity){for(Class<?> owner=entity.getClass();owner!=null;owner=owner.getSuperclass())if(owner.getSimpleName().equals("EntityGlyphid"))return true;return false;}
    @Override public void move(MoverType movement,Vec3 velocity){}
    @Override public void push(double x,double y,double z){}
    @Override public void setPos(double x,double y,double z){if(tickCount==0)super.setPos(x,y,z);}
    @Override public boolean displayFireAnimation(){return false;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("type",getTypeFluid().getID());tag.putFloat("width",entityData.get(WIDTH));tag.putFloat("height",entityData.get(HEIGHT));}
    @Override protected void readAdditionalSaveData(CompoundTag tag){setType(HbmFluids.fromID(tag.getInt("type")));setArea(tag.getFloat("width"),tag.getFloat("height"));}
    public enum SprayStyle{MIST,GAS,NULL}
    public static SprayStyle getStyleFromType(HbmFluidType type){if(type.hasTrait("viscous"))return SprayStyle.NULL;if(type.hasTrait("gaseous")||type.hasTrait("gaseous_art"))return SprayStyle.GAS;return type.hasTrait("liquid")?SprayStyle.MIST:SprayStyle.NULL;}
    public void teleportRandomly(Entity entity){teleportTo(entity,getX()+(random.nextDouble()-.5)*64,getY()+random.nextInt(64)-32,getZ()+(random.nextDouble()-.5)*64);}
    public void teleportTo(Entity entity,double x,double y,double z){Vec3 previous=entity.position();entity.setPosRaw(x,y,z);boolean accepted=false;int bx=Mth.floor(x),by=Mth.floor(y),bz=Mth.floor(z);
        if(level().hasChunkAt(new BlockPos(bx,by,bz))){boolean ground=false;while(!ground&&by>level().getMinBuildHeight()){if(LegacyMaterials.of(level().getBlockState(new BlockPos(bx,by-1,bz))).blocksMovement())ground=true;else{entity.setPosRaw(entity.getX(),entity.getY()-1,entity.getZ());by--;}}
            if(ground){entity.setPos(entity.getX(),entity.getY(),entity.getZ());accepted=level().noCollision(entity,entity.getBoundingBox())&&!level().containsAnyLiquid(entity.getBoundingBox());}}
        if(!accepted){entity.setPos(previous);return;}
        if(entity instanceof ServerPlayer player)player.connection.teleport(entity.getX(),entity.getY(),entity.getZ(),entity.getYRot(),entity.getXRot());
        for(int i=0;i<128;i++){double time=(double)i/127;float vx=(random.nextFloat()-.5F)*.2F,vy=(random.nextFloat()-.5F)*.2F,vz=(random.nextFloat()-.5F)*.2F;level().addParticle(ParticleTypes.PORTAL,previous.x+(entity.getX()-previous.x)*time+(random.nextDouble()-.5)*entity.getBbWidth()*2,previous.y+(entity.getY()-previous.y)*time+random.nextDouble()*entity.getBbHeight(),previous.z+(entity.getZ()-previous.z)*time+(random.nextDouble()-.5)*entity.getBbWidth()*2,vx,vy,vz);}
        level().playSound(null,previous.x,previous.y,previous.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.NEUTRAL,1,1);entity.playSound(SoundEvents.ENDERMAN_TELEPORT,1,1);
    }
}
