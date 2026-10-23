package com.hbm.weapon.sedna.gun.utility;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.world.structure.dependencies.materials.SlagBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.*;
/** Source hook and demolition rounds; the one existing MK4 engine retains all movement/hit synchronization. */
public final class ChargeThrowerBullets {
 public static final BulletConfig HOOK=new BulletConfig("hbm:tool/ct_hook").setItem(PistolAmmo.CT_HOOK).setRenderRotations(false).setLife(6000).setVel(3F).setGrav(.035D).setDoesPenetrate(true).setDamageFalloffByPen(false)
  .setOnUpdate(entity->{var bullet=(EntityBulletBaseMK4)entity;
   if(!entity.level().isClientSide&&entity.tickCount<2&&bullet.getThrower() instanceof Player player&&player.getMainHandItem().getItem() instanceof ItemGunChargeThrower)ItemGunChargeThrower.setLastHook(player.getMainHandItem(),entity.getId());
   entity.noCulling=true;
  }).setOnImpact((bullet,hit)->{if(hit instanceof BlockHitResult block){var point=hit.getLocation().add(ItemGunChargeThrower.normalize(bullet.getDeltaMovement().scale(-1)).scale(.05));bullet.setPos(point);var pos=block.getBlockPos();bullet.getStuck(pos.getX(),pos.getY(),pos.getZ(),block.getDirection().get3DDataValue());}});
 public static final BulletConfig MORTAR=new BulletConfig("hbm:tool/ct_mortar").setItem(PistolAmmo.CT_MORTAR).setDamage(2.5F).setLife(200).setVel(3F).setGrav(.035D).setOnImpact((bullet,hit)->explode(bullet,hit,false));
 public static final BulletConfig HEAVY_MORTAR=new BulletConfig("hbm:tool/ct_mortar_charge").setItem(PistolAmmo.CT_MORTAR_CHARGE).setDamage(5F).setLife(200).setVel(3F).setGrav(.035D).setOnImpact((bullet,hit)->explode(bullet,hit,true));
 private static void explode(EntityBulletBaseMK4 bullet,HitResult hit,boolean heavy){
  if(hit instanceof EntityHitResult entity&&bullet.tickCount<3&&entity.getEntity()==bullet.getThrower())return;
  var point=hit.getLocation();var explosion=new ExplosionVNT(bullet.level(),point.x,point.y,point.z,heavy?15:5,bullet.getThrower());
  if(heavy){
   var slag=HbmContent.block("block_slag");if(!(slag instanceof SlagBlock sourceSlag))throw new IllegalStateException("Charge thrower requires actual source BlockSlag");
   explosion.setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(new BlockMutatorDebris(sourceSlag.fromLegacy(1))));
  }else explosion.setBlockAllocator(new BlockAllocatorBulkie(60,8)).setBlockProcessor(new BlockProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1F));
  explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,bullet.damage).setupPiercing(bullet.config.armorThresholdNegation,bullet.config.armorPiercingPercent)).setPlayerProcessor(new PlayerProcessorStandard());
  if(heavy)LargeConventionalEffects.small(bullet.level(),point.x,point.y+.5,point.z);
  explosion.explode();bullet.setDead();
 }
 public static void init(){}
 private ChargeThrowerBullets(){}
}
