package com.hbm.entity.missile;

import com.hbm.block.LegacyBlockTraits;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.bomb.nuclear.*;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.hazard.HbmHazards;
import com.hbm.missile.completion.MissileContent;
import com.hbm.missile.completion.MissileImpactEffects;
import com.hbm.world.taint.TaintContent;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Actual six source Tier0 warheads, sharing the existing NT path and original micro debris. */
public abstract class EntityMissileTier0 extends EntityMissileBaseNT {
 public EntityMissileTier0(EntityType<?> type,Level level){super(type,level);}
 private static ItemStack material(String id,int amount,int metadata){var stack=new ItemStack(MissileContent.item(id),amount);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
 @Override public List<ItemStack> getDebris(){return new ArrayList<>(List.of(material("wire_fine",4,1300),new ItemStack(MissileContent.item("plate_titanium"),4),material("shell",2,1300),new ItemStack(MissileContent.item("ducttape"))));}
 @Override protected float getContrailScale(){return .5F;}
 public static class EntityMissileTest extends EntityMissileTier0 {
  public EntityMissileTest(EntityType<?> type,Level level){super(type,level);}public EntityMissileTest(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_test"),level);initialize(x,y,z,a,b);}
  @Override public ItemStack getDebrisRareDrop(){return null;}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_test"));}
  @Override public void onMissileImpact(HitResult hit){int x=Mth.floor(getX()),y=Mth.floor(getY()),z=Mth.floor(getZ()),range=50;var slaked=HbmWorldFluids.block("sellafield_slaked");for(int ix=-range;ix<=range;ix++)for(int iy=-range;iy<=range;iy++)for(int iz=-range;iz<=range;iz++){double dist=Math.sqrt(ix*ix+iy*iy+iz*iz);if(dist>range)continue;var pos=new BlockPos(x+ix,y+iy,z+iz);var state=level().getBlockState(pos);int charMeta=(int)Mth.clamp(12-(dist/range)*(dist/range)*13,0,12);if(LegacyBlockTraits.isNormalCube(level(),pos,state)){if(!state.is(slaked)||state.getValue(LegacyOreBlock.LEGACY_META)<charMeta)level().setBlock(pos,slaked.defaultBlockState().setValue(LegacyOreBlock.LEGACY_META,charMeta),3);}else level().setBlock(pos,Blocks.AIR.defaultBlockState(),3);}}
 }
 public static class EntityMissileMicro extends EntityMissileTier0 {
  public EntityMissileMicro(EntityType<?> type,Level level){super(type,level);}public EntityMissileMicro(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_micro"),level);initialize(x,y,z,a,b);}
  @Override public void onMissileImpact(HitResult hit){ExplosionNukeSmall.explode(level(),getX(),getY()+.5,getZ(),ExplosionNukeSmall.PARAMS_HIGH);}
  @Override public ItemStack getDebrisRareDrop(){return com.hbm.weapon.sedna.ammo.FlatAmmoContent.standardStack(NUKE_HIGH_METADATA,1);}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_micro"));}
 }
 public static class EntityMissileSchrabidium extends EntityMissileTier0 {
  public EntityMissileSchrabidium(EntityType<?> type,Level level){super(type,level);}public EntityMissileSchrabidium(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_schrabidium"),level);initialize(x,y,z,a,b);}
  @Override public void onMissileImpact(HitResult hit){int radius=NuclearConfig.radius("aSchrab");var ex=NuclearExplosionMK3.statFacFleija(level(),getX(),getY(),getZ(),radius);if(!ex.isRemoved()){level().addFreshEntity(ex);var cloud=new NuclearFleijaCloud(NuclearContent.FLEIJA_CLOUD.get(),level(),radius);cloud.setPos(position());level().addFreshEntity(cloud);}}
  @Override public ItemStack getDebrisRareDrop(){return null;}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_schrabidium"));}
 }
 public static class EntityMissileBHole extends EntityMissileTier0 {
  public EntityMissileBHole(EntityType<?> type,Level level){super(type,level);}public EntityMissileBHole(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_bhole"),level);initialize(x,y,z,a,b);}
  @Override public void onMissileImpact(HitResult hit){MissileImpactEffects.vanilla(level(),this,getX(),getY(),getZ(),1.5F,false,true);var hole=new EntityBlackHole(level(),1.5F);hole.setPos(position());level().addFreshEntity(hole);}
  @Override public ItemStack getDebrisRareDrop(){return new ItemStack(MissileContent.item("black_hole"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_bhole"));}
 }
 public static class EntityMissileTaint extends EntityMissileTier0 {
  public EntityMissileTaint(EntityType<?> type,Level level){super(type,level);}public EntityMissileTaint(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_taint"),level);initialize(x,y,z,a,b);}
  @Override public void onMissileImpact(HitResult hit){var vec=hit.getLocation();MissileImpactEffects.vanilla(level(),this,vec.x,vec.y,vec.z,5F,false,true);var impact=hit instanceof BlockHitResult block?block.getBlockPos():BlockPos.ZERO;for(int i=0;i<100;i++){var pos=new BlockPos(random.nextInt(11)+impact.getX()-5,random.nextInt(11)+impact.getY()-5,random.nextInt(11)+impact.getZ()-5);var state=level().getBlockState(pos);if(LegacyBlockTraits.isNormalCube(level(),pos,state)&&!state.isAir())level().setBlock(pos,TaintContent.TAINT.get().defaultBlockState(),2);}}
  @Override public ItemStack getDebrisRareDrop(){return new ItemStack(MissileContent.item("powder_spark_mix"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_taint"));}
 }
 public static class EntityMissileEMP extends EntityMissileTier0 {
  public EntityMissileEMP(EntityType<?> type,Level level){super(type,level);}public EntityMissileEMP(Level level,float x,float y,float z,int a,int b){this(MissileContent.type("missile_emp"),level);initialize(x,y,z,a,b);}
  @Override public void onMissileImpact(HitResult hit){ExplosionNukeGeneric.empBlast(level(),(int)getX(),(int)getY(),(int)getZ(),50);var wave=new EntityEMPBlast(level(),50);wave.setPos(position());level().addFreshEntity(wave);}
  @Override public ItemStack getDebrisRareDrop(){return new ItemStack(MissileContent.item("emp_bomb"));}@Override public ItemStack getMissileItemForInfo(){return new ItemStack(MissileContent.item("missile_emp"));}
 }
 private static final int NUKE_HIGH_METADATA=75;
}
