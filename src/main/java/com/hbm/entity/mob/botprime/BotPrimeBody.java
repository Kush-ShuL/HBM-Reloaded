package com.hbm.entity.mob.botprime;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
/** All74source segments are actual LivingEntity collision/attack targets; damage follows the real linked predecessor,not74fake meshes. */
public final class BotPrimeBody extends BotPrimeBase {
 public BotPrimeBody(EntityType<? extends BotPrimeBody> type,Level level){super(type,level);}
 @Override public boolean isHead(){return false;}@Override public float contactDamage(Entity target){return target instanceof LivingEntity living?living.getHealth()*.75F:100;}@Override public boolean canBeAffected(MobEffectInstance effect){return false;}
 @Override protected void customServerAiStep(){wormActionState();targeting.tick(false);updateBodyMovement();if(didCheck){if(targeted==null||!targeted.isAlive())setHealth(getHealth()-1999);if((followed==null||!followed.isAlive())&&random.nextInt(60)==0)level().explode(this,getX(),getY(),getZ(),2,false,Level.ExplosionInteraction.NONE);}if(followed!=null&&followed.isAlive()&&getTarget()!=null){if(seeThroughNonSolids(getTarget())){if(++attackCounter==10){laserAttack(getTarget(),false);attackCounter=-20;}}else if(attackCounter>0)attackCounter--;}else if(attackCounter>0)attackCounter--;if(targeted!=null)faceMotion(targeted.position().subtract(position()));}
 @Override public void tick(){super.tick();if(targeted!=null)faceMotion(targeted.position().subtract(position()));}
 @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("partID",partNumber);}@Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);partNumber=tag.getInt("partID");}
}
