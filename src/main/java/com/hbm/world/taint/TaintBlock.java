package com.hbm.world.taint;

import com.hbm.entity.mob.crab.TeslaCrab;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.world.taint.mixin.TaintFallingFactory;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.*;

/** Original metadata-limited spread order, partial collision, uncurable taint and exact-class mob conversion. */
public final class TaintBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final VoxelShape COLLISION=Block.box(0,0,0,16,12,16);
 private static final Direction[] DIRECTIONS={Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};
 public TaintBlock(){super(Properties.of().mapColor(MapColor.COLOR_GRAY).strength(15,6).randomTicks().requiresCorrectToolForDrops().isSuffocating((s,l,p)->true).isViewBlocking((s,l,p)->true));registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return COLLISION;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);var ench=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);return tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(ench.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(new ItemStack(this)):List.of();}
 @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){spread(state,level,pos,random);}
 @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){spread(state,level,pos,random);}
 private void spread(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
  int meta=state.getValue(META);if(meta>=15)return;
  for(int i=-3;i<=3;i++)for(int j=-3;j<=3;j++)for(int k=-3;k<=3;k++){
   if(Math.abs(i)+Math.abs(j)+Math.abs(k)>4)continue;if(random.nextFloat()>.25F)continue;
   var target=pos.offset(i,j,k);var before=level.getBlockState(target);if(before.isAir()||before.is(Blocks.BEDROCK))continue;
   int next=meta+1;boolean air=false;for(var direction:DIRECTIONS)if(level.getBlockState(target.relative(direction)).isAir()){air=true;break;}if(!air)next=meta+3;
   if(next>15||before.is(this)&&before.getValue(META)>=next)continue;
   var placed=defaultBlockState().setValue(META,next);level.setBlock(target,placed,3);
   if(random.nextFloat()<.25F&&sourceFallsThrough(level.getBlockState(target.below()))){
    var falling=TaintFallingFactory.hbm$taintFalling(level,target.getX()+.5,target.getY()+.5,target.getZ()+.5,placed);
    falling.getPersistentData().putBoolean("hbmSourceTaintFall",true);level.addFreshEntity(falling);
   }
  }
 }
 private static boolean sourceFallsThrough(BlockState state){return state.isAir()||state.is(Blocks.FIRE)||state.is(Blocks.WATER)||state.is(Blocks.LAVA);}
 @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){
  var motion=entity.getDeltaMovement();entity.setDeltaMovement(motion.x*.6,motion.y,motion.z*.6);
  if(entity instanceof LivingEntity living&&level.random.nextInt(50)==0){var effect=new MobEffectInstance(MedicineEffects.TAINT,15*20,15-state.getValue(META));effect.getCures().clear();living.addEffect(effect);}
  if(!level.isClientSide){Entity replacement=null;if(entity.getClass()==Creeper.class)replacement=TaintContent.CREEPER.get().create(level);else if(entity instanceof TeslaCrab)replacement=TaintContent.CRAB.get().create(level);if(replacement!=null){replacement.moveTo(entity.getX(),entity.getY(),entity.getZ(),entity.getYRot(),entity.getXRot());entity.discard();level.addFreshEntity(replacement);}}
 }
}
