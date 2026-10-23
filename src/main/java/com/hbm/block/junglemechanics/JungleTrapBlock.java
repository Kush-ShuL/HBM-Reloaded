package com.hbm.block.junglemechanics;
import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.*;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;
/** All fifteen exact TrappedBrick source variants, including its intentionally empty poison-dart action. */
public final class JungleTrapBlock extends BaseEntityBlock implements JungleMetadataBlock {
 public enum TrapType{ON_STEP,DETECTOR}
 public enum Trap {
  FALLING_ROCKS(TrapType.DETECTOR),FIRE(TrapType.ON_STEP),ARROW(TrapType.DETECTOR),SPIKES(TrapType.ON_STEP),MINE(TrapType.ON_STEP),WEB(TrapType.ON_STEP),FLAMING_ARROW(TrapType.DETECTOR),PILLAR(TrapType.DETECTOR),RAD_CONVERSION(TrapType.ON_STEP),MAGIC_CONVERSTION(TrapType.ON_STEP),SLOWNESS(TrapType.ON_STEP),WEAKNESS(TrapType.ON_STEP),POISON_DART(TrapType.DETECTOR),ZOMBIE(TrapType.DETECTOR),SPIDERS(TrapType.DETECTOR);
  public final TrapType type;Trap(TrapType type){this.type=type;}public static Trap get(int value){return value>=0&&value<values().length?values()[value]:FIRE;}
 }
 public JungleTrapBlock(){super(JungleMechanicsContent.properties(0));registerDefaultState(stateDefinition.any().setValue(JungleMechanicsContent.META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(JungleMechanicsContent.META);}
 @Override public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(JungleMechanicsContent.META,metadata);}@Override public int sourceVariants(){return Trap.values().length;}
 @Override @Nullable public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return Trap.get(state.getValue(JungleMechanicsContent.META)).type==TrapType.DETECTOR?new JungleTrapBlockEntity(pos,state):null;}
 @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide||Trap.get(state.getValue(JungleMechanicsContent.META)).type!=TrapType.DETECTOR?null:createTickerHelper(type,JungleMechanicsContent.TRAP_ENTITY.get(),JungleTrapBlockEntity::tick);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return JungleDrops.drops(this,state,params,0,true);}
 @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(asItem());}
 @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){
  var trap=Trap.get(state.getValue(JungleMechanicsContent.META));if(level.isClientSide||trap.type!=TrapType.ON_STEP||!(entity instanceof Player player))return;
  var above=pos.above();switch(trap){
   case FIRE->{if(level.getBlockState(above).canBeReplaced())level.setBlock(above,Blocks.FIRE.defaultBlockState(),3);}
   case SPIKES->{if(level.getBlockState(above).canBeReplaced())level.setBlock(above,HbmContent.block("spikes").defaultBlockState(),3);for(var target:level.getEntities((Entity)null,new net.minecraft.world.phys.AABB(pos.getX(),pos.getY()+1,pos.getZ(),pos.getX()+1,pos.getY()+2,pos.getZ()+1)))target.hurt(HbmDamageSources.source(level,HbmDamageSources.spikes),10);level.playSound(null,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,HbmSounds.get("entity.slicer"),SoundSource.BLOCKS,1,1);}
   case MINE->level.explode(null,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,1,false,Level.ExplosionInteraction.NONE);
   case WEB->{if(level.getBlockState(above).canBeReplaced())level.setBlock(above,Blocks.COBWEB.defaultBlockState(),3);}
   case RAD_CONVERSION,MAGIC_CONVERSTION->{for(int x=-3;x<=3;x++)for(int y=-3;y<=3;y++)for(int z=-3;z<=3;z++){if(level.random.nextBoolean())continue;var target=pos.offset(x,y,z);var block=level.getBlockState(target).getBlock();if(block==HbmContent.block("brick_jungle")||block==HbmContent.block("brick_jungle_cracked")||block==HbmContent.block("brick_jungle_lava"))level.setBlock(target,(trap==Trap.RAD_CONVERSION?JungleMechanicsContent.OOZE:JungleMechanicsContent.MYSTIC).get().defaultBlockState(),3);}}
   case SLOWNESS->player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,300,2));
   case WEAKNESS->player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,300,2));
   default->{}
  }
  finish(level,pos);
 }
 static void finish(Level level,BlockPos pos){level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.DISPENSER_DISPENSE,SoundSource.BLOCKS,.3F,.6F);level.setBlock(pos,HbmContent.block("brick_jungle").defaultBlockState(),3);}
}
