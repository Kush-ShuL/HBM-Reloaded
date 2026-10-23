package com.hbm.block.digamma;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;

/** Original transient portal-material matter:18 adjacent cells, metadata-limited spread and randomized selection/render bounds. */
public final class DigammaMatterBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private static final Random RANDOM=new Random();
 public DigammaMatterBlock(){super(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(-1,10800000).noCollission().noOcclusion().dynamicShape());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public static float[] sourceBounds(){return new float[]{RANDOM.nextInt(9)/16F,RANDOM.nextInt(9)/16F,RANDOM.nextInt(9)/16F,1-RANDOM.nextInt(9)/16F,1-RANDOM.nextInt(9)/16F,1-RANDOM.nextInt(9)/16F};}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){var b=sourceBounds();return Shapes.box(b[0],b[1],b[2],b[3],b[4],b[5]);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){if(!old.is(this)&&!level.isClientSide)level.scheduleTick(pos,this,10+RANDOM.nextInt(40));}
 @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
  int meta=state.getValue(META);level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);if(meta>=7)return;
  for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){
   int distance=Math.abs(x)+Math.abs(y)+Math.abs(z);
   if(distance>0&&distance<3){var target=pos.offset(x,y,z);if(!level.getBlockState(target).is(this))level.setBlock(target,defaultBlockState().setValue(META,meta+1),3);}
  }
 }
}
