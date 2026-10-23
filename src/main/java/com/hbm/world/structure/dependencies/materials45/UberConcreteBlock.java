package com.hbm.world.structure.dependencies.materials45;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
/** BlockUberConcrete: all16 source aging stages, inverse-stage RNG and real falling debris. No invented explosion immunity. */
public final class UberConcreteBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public UberConcreteBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(150,600).sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 @Override protected void randomTick(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){advance(state,world,pos,random);}
 @Override protected void tick(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){advance(state,world,pos,random);}
 private void advance(BlockState state,ServerLevel world,BlockPos pos,RandomSource random){
  int metadata=state.getValue(META);if(random.nextInt(metadata+1)>0)return;
  if(metadata<15){world.setBlock(pos,state.setValue(META,metadata+1),3);return;}
  world.setBlock(pos,Blocks.AIR.defaultBlockState(),3);var broken=HbmContent.block("concrete_super_broken").defaultBlockState();
  if(world.getBlockState(pos.below()).isAir()){world.setBlock(pos,broken,3);return;}
  var sides=new ArrayList<>(List.of(Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST));
  // Original Collections.shuffle(sides) deliberately does not consume the passed world RNG.
  Collections.shuffle(sides);
  for(var side:sides){var next=pos.relative(side);if(world.getBlockState(next).isAir()&&world.getBlockState(next.below()).isAir()){
   // Vanilla's modern bottom-coordinate constructor is the source center-height falling box.
   var debris=FallingBlockEntity.fall(world,next,broken);debris.time=2;debris.dropItem=false;debris.setHurtsEntities(2F,40);return;
  }}
  world.setBlock(pos,broken,3);
 }
}
