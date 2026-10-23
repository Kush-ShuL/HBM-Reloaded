package com.hbm.entity.mob;
import com.hbm.entity.mob.sourcecreepers.SourceCreeper;
import com.hbm.damage.DamageResistanceHandler;
import com.hbm.entity.effect.EntityMist;
import com.hbm.fluid.HbmFluids;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class EntityCreeperPhosgene extends SourceCreeper {
 public EntityCreeperPhosgene(EntityType<? extends EntityCreeperPhosgene> type,Level level){super(type,level,20);}
 @Override public boolean hurt(DamageSource source,float amount){if(!DamageResistanceHandler.isAbsolute(source)&&!source.is(DamageTypeTags.BYPASSES_ARMOR))amount-=4;if(amount<0)return false;return super.hurt(source,amount);}
 @Override public void sourceExplosion(){if(level().isClientSide)return;discard();level().explode(this,getX(),getY()+getBbHeight()/2,getZ(),2,false,Level.ExplosionInteraction.NONE);var mist=new EntityMist(level()).setType(HbmFluids.PHOSGENE).setArea(10,5).setDuration(150);mist.setPos(position());level().addFreshEntity(mist);}
}
