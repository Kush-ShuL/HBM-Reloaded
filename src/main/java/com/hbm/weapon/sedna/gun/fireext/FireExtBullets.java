package com.hbm.weapon.sedna.gun.fireext;
import com.hbm.api.machine.IRepairable;
import com.hbm.api.machine.IRepairable.EnumExtinguishType;
import com.hbm.block.falling.LegacyFallingRules;
import com.hbm.block.fluid.*;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.machine.arcfurnace.ArcSandBlock;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunAmmo;
import java.util.function.BiConsumer;
import net.minecraft.core.*;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
/** Original three XFactoryTool sprays through the sole MK4 movement/authority/config synchronization engine. */
public final class FireExtBullets {
 /** Client-only particle constructor is installed by the real client subscriber; common never loads Minecraft classes. */
 public static BiConsumer<EntityBulletBaseMK4,Integer> CLIENT_DUST=(bullet,mode)->{};
 private static GunAmmo ammo(int metadata){return new GunAmmo(FireExtContent.AMMO,1,metadata);}
 private static BulletConfig base(String id,int metadata,float spread){return new BulletConfig("hbm:tool/"+id).setItem(ammo(metadata)).setReloadCount(300).setLife(100).setVel(.75F).setGrav(.04D).setSpread(spread)
  .setOnEntityHit((bullet,hit)->{if(hit instanceof EntityHitResult entity)entity.getEntity().clearFire();});}
 public static final BulletConfig WATER=base("fext_water",0,.025F).setOnUpdate(entity->{var bullet=(EntityBulletBaseMK4)entity;
  if(entity.level().isClientSide){CLIENT_DUST.accept(bullet,0);return;}
  var pos=entity.blockPosition();var state=entity.level().getBlockState(pos);
  if(state.is(HbmWorldFluids.block("volcanic_lava_block"))&&state.getValue(LegacyFluidBlock.LEVEL)==0){entity.level().setBlock(pos,Blocks.OBSIDIAN.defaultBlockState(),3);bullet.setDead();}
 }).setOnRicochet(FireExtBullets::water);
 public static final BulletConfig FOAM=base("fext_foam",1,.05F).setOnUpdate(entity->{if(entity.level().isClientSide)CLIENT_DUST.accept((EntityBulletBaseMK4)entity,1);}).setOnRicochet(FireExtBullets::foam);
 public static final BulletConfig SAND=base("fext_sand",2,.05F).setOnUpdate(entity->{if(entity.level().isClientSide)CLIENT_DUST.accept((EntityBulletBaseMK4)entity,2);}).setOnRicochet(FireExtBullets::sand);
 // Legacy MovingObjectPosition's entity constructor leaves blockX/Y/Z/sideHit at Java's zero defaults.
 // onRicochet is invoked for entity hits too. Keep this exact source quirk rather than inventing a new hit-position branch.
 private static BlockPos impactPos(HitResult hit){return hit instanceof BlockHitResult block?block.getBlockPos():BlockPos.ZERO;}
 private static Direction impactSide(HitResult hit){return hit instanceof BlockHitResult block?block.getDirection():Direction.DOWN;}
 private static IRepairable repairable(Level level,BlockPos pos){var state=level.getBlockState(pos);var core=state.getBlock() instanceof DummyableBlock dummy?dummy.findCore(level,pos):pos;return core!=null&&level.getBlockEntity(core) instanceof IRepairable machine?machine:null;}
 private static boolean fire(BlockState state){return LegacyFallingRules.material(state).name().equals("fire");}
 private static boolean replaceable(BlockState state){if(state.getBlock() instanceof ExtinguishingLayerBlock layer)return layer.sourceReplaceable(state);if(state.getBlock() instanceof SnowLayerBlock)return state.getValue(SnowLayerBlock.LAYERS)<8;return LegacyFallingRules.material(state).isReplaceable();}
 private static void fizz(EntityBulletBaseMK4 bullet){bullet.level().playSound(null,bullet.getX(),bullet.getY(),bullet.getZ(),SoundEvents.FIRE_EXTINGUISH,SoundSource.NEUTRAL,1F,1.5F+bullet.level().random.nextFloat()*.5F);}
 private static void water(EntityBulletBaseMK4 bullet,HitResult hit){
  var level=bullet.level();if(level.isClientSide)return;var pos=impactPos(hit);boolean fizz=false;
  for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){var at=pos.offset(x,y,z);var state=level.getBlockState(at);if(state.is(Blocks.FIRE)||state.is(FireExtContent.FOAM_LAYER.get())||state.is(HbmContent.block("block_foam"))){level.setBlock(at,Blocks.AIR.defaultBlockState(),3);fizz=true;}}
  var machine=repairable(level,pos);if(machine!=null)machine.tryExtinguish(level,pos,EnumExtinguishType.WATER);
  if(fizz)fizz(bullet);bullet.setDead();
 }
 private static void foam(EntityBulletBaseMK4 bullet,HitResult hit){
  var level=bullet.level();if(level.isClientSide)return;var pos=impactPos(hit);boolean fizz=false;
  for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){var at=pos.offset(x,y,z);if(fire(level.getBlockState(at))){level.setBlock(at,Blocks.AIR.defaultBlockState(),3);fizz=true;}}
  var machine=repairable(level,pos);if(machine!=null){machine.tryExtinguish(level,pos,EnumExtinguishType.FOAM);return;}
  if(level.random.nextBoolean())pos=pos.relative(impactSide(hit));var state=level.getBlockState(pos);var layer=FireExtContent.FOAM_LAYER.get();
  if(replaceable(state)&&layer.sourceCanPlace(level,pos)){
   if(!state.is(layer))level.setBlock(pos,layer.defaultBlockState(),3);
   else{int metadata=state.getValue(ExtinguishingLayerBlock.META);if(metadata<6)level.setBlock(pos,state.setValue(ExtinguishingLayerBlock.META,metadata+1),3);else level.setBlock(pos,HbmContent.block("block_foam").defaultBlockState(),3);}
  }
  if(fizz)fizz(bullet); // Source deliberately leaves this projectile alive.
 }
 private static void sand(EntityBulletBaseMK4 bullet,HitResult hit){
  var level=bullet.level();if(level.isClientSide)return;var pos=impactPos(hit);var machine=repairable(level,pos);if(machine!=null){machine.tryExtinguish(level,pos,EnumExtinguishType.SAND);return;}
  if(level.random.nextBoolean())pos=pos.relative(impactSide(hit));var state=level.getBlockState(pos);var layer=FireExtContent.BORON_LAYER.get();
  if((replaceable(state)||state.is(layer))&&layer.sourceCanPlace(level,pos)){
   if(!state.is(layer))level.setBlock(pos,layer.defaultBlockState(),3);
   else{int metadata=state.getValue(ExtinguishingLayerBlock.META);if(metadata<6)level.setBlock(pos,state.setValue(ExtinguishingLayerBlock.META,metadata+1),3);else{var full=HbmContent.block("sand_mix");if(!(full instanceof ArcSandBlock sand))throw new IllegalStateException("Extinguisher requires actual source BlockNTMSand");level.setBlock(pos,sand.fromLegacy(0),3);}}
   if(fire(state))fizz(bullet);
  }
 }
 public static void init(){}
 private FireExtBullets(){}
}
