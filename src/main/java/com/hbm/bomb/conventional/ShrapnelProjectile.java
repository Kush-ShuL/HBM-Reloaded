package com.hbm.bomb.conventional;

import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.block.gas.GasContent;
import com.hbm.block.gas.GasKind;
import com.hbm.damage.HbmDamageSources;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.phys.*;

/** Source EntityShrapnel and its 1.7.10 vanilla EntityThrowable simulation, rather than a modern arrow. */
public final class ShrapnelProjectile extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Byte> STYLE=SynchedEntityData.defineId(ShrapnelProjectile.class,EntityDataSerializers.BYTE);
    private LivingEntity thrower;private int ticksInAir;
    public ShrapnelProjectile(EntityType<? extends ShrapnelProjectile> type,Level level){super(type,level);}
    public ShrapnelProjectile(Level level){this(ConventionalProjectiles.SHRAPNEL.get(),level);}
    public ShrapnelProjectile(Level level,double x,double y,double z){this(level);setPos(x,y,z);}
    public ShrapnelProjectile(Level level,LivingEntity owner){this(level);thrower=owner;moveTo(owner.getX(),owner.getY()+owner.getEyeHeight(),owner.getZ(),owner.getYRot(),owner.getXRot());setPos(getX()-Mth.cos(getYRot()/180F*(float)Math.PI)*.16F,getY()-.1D,getZ()-Mth.sin(getYRot()/180F*(float)Math.PI)*.16F);setThrowableHeading(-Mth.sin(getYRot()/180F*(float)Math.PI)*Mth.cos(getXRot()/180F*(float)Math.PI)*.4F,-Mth.sin(getXRot()/180F*(float)Math.PI)*.4F,Mth.cos(getYRot()/180F*(float)Math.PI)*Mth.cos(getXRot()/180F*(float)Math.PI)*.4F,1.5F,1);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(STYLE,(byte)0);}
    public int style(){return entityData.get(STYLE);}
    @Override public AABB getBoundingBoxForCulling(){return getBoundingBox().inflate(style()>=2?1:.35);}
    public void setTrail(boolean value){entityData.set(STYLE,(byte)(value?1:0));}public void setVolcano(boolean value){entityData.set(STYLE,(byte)(value?2:0));}public void setWatz(boolean value){entityData.set(STYLE,(byte)(value?3:0));}public void setRadVolcano(boolean value){entityData.set(STYLE,(byte)(value?4:0));}
    @Override public LivingEntity getOwner(){return thrower;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){double perimeter=getBoundingBox().getSize()*4*64;return distance<perimeter*perimeter;}
    public void setThrowableHeading(double x,double y,double z,float speed,float inaccuracy){float length=(float)Math.sqrt(x*x+y*y+z*z);x=x/length+random.nextGaussian()*(double).0075F*inaccuracy;y=y/length+random.nextGaussian()*(double).0075F*inaccuracy;z=z/length+random.nextGaussian()*(double).0075F*inaccuracy;setDeltaMovement(x*speed,y*speed,z*speed);updateInitialRotation();}
    private void updateInitialRotation(){var v=getDeltaMovement();float horizontal=(float)Math.sqrt(v.x*v.x+v.z*v.z);setYRot((float)(Math.atan2(v.x,v.z)*180D/(double)(float)Math.PI));setXRot((float)(Math.atan2(v.y,horizontal)*180D/(double)(float)Math.PI));yRotO=getYRot();xRotO=getXRot();}
    @Override public void lerpMotion(double x,double y,double z){setDeltaMovement(x,y,z);if(xRotO==0&&yRotO==0)updateInitialRotation();}
    @Override public void tick(){
        super.tick();ticksInAir++;Vec3 from=position(),motion=getDeltaMovement(),to=from.add(motion);
        // Vanilla's old default ray trace includes non-colliding block outlines; the NTM variant deliberately differs.
        var block=level().clip(new ClipContext(from,to,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,this));HitResult impact=block.getType()==HitResult.Type.MISS?null:block;if(impact!=null)to=impact.getLocation();
        if(!level().isClientSide){Entity nearestEntity=null;double nearest=0;for(var entity:level().getEntities(this,getBoundingBox().expandTowards(motion).inflate(1))){if(!entity.isPickable()||entity==thrower&&ticksInAir<5)continue;var hit=entity.getBoundingBox().inflate((double).3F).clip(from,to);if(hit.isPresent()){double distance=from.distanceTo(hit.get());if(distance<nearest||nearest==0){nearestEntity=entity;nearest=distance;}}}if(nearestEntity!=null)impact=new EntityHitResult(nearestEntity);}
        if(impact!=null){if(impact instanceof BlockHitResult hit&&level().getBlockState(hit.getBlockPos()).getBlock() instanceof Portal portal)setAsInsidePortal(portal,hit.getBlockPos());else onImpact(impact);}
        setPos(position().add(getDeltaMovement()));motion=getDeltaMovement();float horizontal=(float)Math.sqrt(motion.x*motion.x+motion.z*motion.z);setYRot((float)(Math.atan2(motion.x,motion.z)*180D/(double)(float)Math.PI));setXRot((float)(Math.atan2(motion.y,horizontal)*180D/(double)(float)Math.PI));
        while(getXRot()-xRotO< -180)xRotO-=360;while(getXRot()-xRotO>=180)xRotO+=360;while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;setXRot(xRotO+(getXRot()-xRotO)*.2F);setYRot(yRotO+(getYRot()-yRotO)*.2F);
        float drag=.99F;if(isInWater()){for(int i=0;i<4;i++)level().addParticle(ParticleTypes.BUBBLE,getX()-motion.x*.25F,getY()-motion.y*.25F,getZ()-motion.z*.25F,motion.x,motion.y,motion.z);drag=.8F;}setDeltaMovement(motion.scale(drag).add(0,-(double).03F,0));setPos(getX(),getY(),getZ());
        if(level().isClientSide&&style()==1)level().addParticle(ParticleTypes.FLAME,getX(),getY(),getZ(),0,0,0);
    }
    private void onImpact(HitResult impact){
        if(impact instanceof EntityHitResult hit)hit.getEntity().hurt(HbmDamageSources.source(level(),HbmDamageSources.shrapnel),15);
        if(tickCount<=5)return;if(!level().isClientSide)discard();
        // Old entity-hit MovingObjectPosition leaves its integer block coordinates at zero.
        BlockPos pos=impact instanceof BlockHitResult hit?hit.getBlockPos():BlockPos.ZERO;int style=style();
        if(style==2||style==4){if(!level().isClientSide){if(getDeltaMovement().y<-.2D){if(level().getBlockState(pos.above()).canBeReplaced())level().setBlockAndUpdate(pos.above(),HbmWorldFluids.block(style==2?"volcanic_lava_block":"rad_lava_block").defaultBlockState());for(int x=-1;x<=1;x++)for(int y=0;y<=2;y++)for(int z=-1;z<=1;z++){var gas=pos.offset(x,y,z);if(level().getBlockState(gas).isAir())level().setBlockAndUpdate(gas,GasContent.BLOCKS_BY_KIND.get(GasKind.MONOXIDE).get().defaultBlockState());}}
            if(getDeltaMovement().y>0){var blast=new ExplosionNT(level(),null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,7);blast.addAttrib(ExAttrib.NODROP);blast.addAttrib(style==2?ExAttrib.LAVA_V:ExAttrib.LAVA_R);blast.addAttrib(ExAttrib.NOSOUND);blast.addAttrib(ExAttrib.ALLMOD);blast.addAttrib(ExAttrib.NOHURT);blast.explode();}}
        }else if(style==3){if(level().getBlockState(pos.above()).canBeReplaced())level().setBlockAndUpdate(pos.above(),HbmWorldFluids.block("mud_block").defaultBlockState());}
        else for(int i=0;i<5;i++)level().addParticle(ParticleTypes.LAVA,getX(),getY(),getZ(),0,0,0);
        if(level().isClientSide)level().playLocalSound(getX(),getY(),getZ(),SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,1,1,false);else level().playSound(null,getX(),getY(),getZ(),SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,1,1);
    }
    @Override public boolean shouldBeSaved(){return false;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}
    @Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
}
