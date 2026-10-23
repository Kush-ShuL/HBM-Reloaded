package com.hbm.entity.projectile;
import com.hbm.weapon.sedna.gun.chemthrower.*;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.fluid.*;
import java.util.function.Consumer;
import net.minecraft.network.syncher.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
/** Actual EntityChemical, using the existing unique EntityThrowableNT simulation (not the missile interpolation base). */
public final class EntityChemical extends EntityThrowableNT implements LegacyTrackingRange {
 private static final EntityDataAccessor<Integer> FLUID=SynchedEntityData.defineId(EntityChemical.class,EntityDataSerializers.INT);public static Consumer<EntityChemical> CLIENT_TICK=entity->{};
 public EntityChemical(EntityType<? extends EntityChemical> type,Level level){super(type,level);noCulling=true;}
 /** Source receives offsets but intentionally ignores all3 in its super(world,thrower) constructor. */
 public EntityChemical(Level level,LivingEntity owner,double ignoredSide,double ignoredHeight,double ignoredForward){this(ChemthrowerContent.CHEMICAL.get(),level);setThrower(owner);moveTo(owner.getX(),owner.getEyeY(),owner.getZ(),owner.getYRot(),owner.getXRot());float yaw=getYRot()/180F*(float)Math.PI,pitch=getXRot()/180F*(float)Math.PI;setPos(getX()-(double)(Mth.cos(yaw)*.16F),getY()-.1D,getZ()-(double)(Mth.sin(yaw)*.16F));float velocity=.4F;setThrowableHeading(-Mth.sin(yaw)*Mth.cos(pitch)*velocity,-Mth.sin(pitch)*velocity,Mth.cos(yaw)*Mth.cos(pitch)*velocity,1.5F,1);}
 @Override public int legacyTrackingRange(){return 1000;}@Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(FLUID,0);}
 public net.minecraft.util.RandomSource sourceRandom(){return random;}
 public EntityChemical setFluid(HbmFluidType fluid){entityData.set(FLUID,fluid.getID());return this;}public HbmFluidType getFluidType(){return HbmFluids.fromID(entityData.get(FLUID));}
 @Override public void tick(){if(!level().isClientSide){if(tickCount>getMaxAge())discard();var type=getFluidType();if(type.hasTrait("gaseous")||type.hasTrait("gaseous_art")){double intensity=1D-(double)tickCount/getMaxAge();for(var entity:level().getEntities(getThrower(),getBoundingBox().inflate(intensity*2.5,intensity*2.5,intensity*2.5)))ChemicalExposure.affect(this,entity,intensity);}}else CLIENT_TICK.accept(this);super.tick();}
 @Override protected void onImpact(HitResult hit){if(level().isClientSide)return;if(hit instanceof EntityHitResult entity)ChemicalExposure.affect(this,entity.getEntity(),1D-(double)tickCount/getMaxAge());if(hit instanceof BlockHitResult block){ChemicalWorldImpact.hit(this,block.getBlockPos());discard();}}
 public ChemicalStyle getStyle(){return getStyleFromType(getFluidType());}
 public static ChemicalStyle getStyleFromType(HbmFluidType type){if(type==HbmFluids.IONGEL)return ChemicalStyle.LIGHTNING;if(type.isAntimatter())return ChemicalStyle.AMAT;if(type.hasTrait("gaseous")||type.hasTrait("gaseous_art"))return type.hasTrait(FluidTraits.Flammable.class)||type.hasTrait(FluidTraits.Combustible.class)?ChemicalStyle.GASFLAME:ChemicalStyle.GAS;if(type.hasTrait("liquid"))return type.hasTrait(FluidTraits.Combustible.class)?ChemicalStyle.BURNING:ChemicalStyle.LIQUID;return ChemicalStyle.NULL;}
 @Override protected float getAirDrag(){return switch(getStyle()){case AMAT,LIGHTNING->1;case GAS->.95F;default->.99F;};}@Override protected float getWaterDrag(){return switch(getStyle()){case AMAT,LIGHTNING,GAS->1;default->.8F;};}
 public int getMaxAge(){return switch(getStyle()){case AMAT,NULL->100;case LIGHTNING->5;case BURNING,LIQUID->600;case GAS->60;case GASFLAME->20;};}@Override public double getGravityVelocity(){return switch(getStyle()){case AMAT,LIGHTNING,GAS->0;case GASFLAME->-.01;default->.03;};}
 public boolean isExtinguishing(){return getStyle()==ChemicalStyle.LIQUID&&getFluidType().temperature<50&&!getFluidType().hasTrait(FluidTraits.Flammable.class);}
 public enum ChemicalStyle{AMAT,LIGHTNING,LIQUID,GAS,GASFLAME,BURNING,NULL}
 // Source stores no fluid in NBT: inherited original throwable state is retained, saved/reloaded fluid defaults to NONE.
}
