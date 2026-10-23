package com.hbm.entity.mob.glyphid;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidItemContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** Original sulfuric breath/mist uses the already-real unique EntityChemical/EntityMist and private HBM fluid. */
public final class EntityGlyphidBehemoth extends EntityGlyphid {
    public int timer=120;int breathTime;
    public EntityGlyphidBehemoth(EntityType<? extends EntityGlyphidBehemoth> type,Level level){super(type,level);}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_behemoth.png");}
    @Override public double sourceScale(){return 1.5;}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getBehemoth();}
    @Override public void tick(){super.tick();if(legacyTarget==null){timer=120;breathTime=0;}else if(breathTime>0){if(!swinging)sourceSwing();acidAttack();setYRot(yRotO);breathTime--;}else if(--timer<=0){breathTime=120;timer=120;}}
    public void acidAttack(){if(!level().isClientSide&&legacyTarget instanceof LivingEntity target&&distanceTo(target)<20){addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40,6));level().addFreshEntity(new EntityChemical(level(),this,0,0,0).setFluid(HbmFluids.SULFURIC_ACID));}}
    @Override public void die(DamageSource source){super.die(source);if(!level().isClientSide){var mist=new EntityMist(level());mist.setType(HbmFluids.SULFURIC_ACID);mist.setPos(getX(),getY(),getZ());mist.setArea(10,4);mist.setDuration(120);level().addFreshEntity(mist);}}
    @Override protected void sourceDrops(boolean recentlyHit,int looting){var gland=sourceItem("glyphid_gland",1);FluidItemContent.setType(gland,HbmFluids.SULFURIC_ACID);spawnAtLocation(gland,1);super.sourceDrops(recentlyHit,looting);}
    @Override public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount*.15,2),100);}
    @Override public int swingDuration(){return 100;}
}
