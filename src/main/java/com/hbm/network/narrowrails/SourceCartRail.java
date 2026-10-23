package com.hbm.network.narrowrails;

import net.minecraft.core.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;

/** Actual RailGeneric / RailBooster. Native RailState is the shared Forge connection engine. */
public final class SourceCartRail extends RailBlock {
 public enum Kind {
  WOOD("rail_wood",.2F,true,"axe"),NARROW("rail_narrow",.4F,true,"pickaxe"),HIGHSPEED("rail_highspeed",1F,false,"pickaxe"),BOOSTER("rail_booster",1F,false,"pickaxe");
  public final String id,tool;public final float maxSpeed;public final boolean turnedTexture;
  Kind(String id,float speed,boolean flexible,String tool){this.id=id;this.maxSpeed=speed;this.turnedTexture=flexible;this.tool=tool;}
 }
 public final Kind kind;
 public SourceCartRail(Kind kind){super(Properties.of().mapColor(MapColor.NONE).noCollission().noOcclusion().strength(5,6).sound(SoundType.STONE).pushReaction(PushReaction.DESTROY));this.kind=kind;}
 @Override public float getRailMaxSpeed(BlockState state,Level level,BlockPos position,AbstractMinecart cart){return kind.maxSpeed;}
 // The original flag only selects the turned icon and lore. Its actual method
 // returned !isPowered(), and all four source constructors pass false.
 @Override public boolean isFlexibleRail(BlockState state,BlockGetter level,BlockPos position){return true;}
 @Override public boolean canMakeSlopes(BlockState state,BlockGetter level,BlockPos position){return true;}
 @Override public void onMinecartPass(BlockState state,Level level,BlockPos position,AbstractMinecart cart){if(kind==Kind.BOOSTER)cart.setDeltaMovement(cart.getDeltaMovement().scale((double)1.15F));}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState();}
 @Override protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,BlockGetter level,BlockPos position,net.minecraft.world.phys.shapes.CollisionContext context){return state.getValue(SHAPE).isAscending()?Block.box(0,0,0,16,10,16):FLAT_AABB;}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos position){return supports(level,position.below());}
 private static boolean supports(BlockGetter level,BlockPos position){return level.getBlockState(position).isFaceSturdy(level,position,Direction.UP);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos position,Block changed,BlockPos from,boolean moved){
  if(!level.isClientSide&&level.getBlockState(position).is(this)){
   var shape=state.getValue(SHAPE);BlockPos uphill=switch(shape){case ASCENDING_EAST->position.east();case ASCENDING_WEST->position.west();case ASCENDING_NORTH->position.north();case ASCENDING_SOUTH->position.south();default->null;};
   if(!supports(level,position.below())||uphill!=null&&!supports(level,uphill)){dropResources(state,level,position);level.removeBlock(position,moved);return;}
  }
  super.neighborChanged(state,level,position,changed,from,moved);
 }
 @Override public boolean canPlaceLiquid(net.minecraft.world.entity.player.Player player,BlockGetter level,BlockPos position,BlockState state,Fluid fluid){return fluid.isSame(Fluids.WATER)||fluid.isSame(Fluids.LAVA);}
 @Override public boolean placeLiquid(LevelAccessor level,BlockPos position,BlockState state,FluidState fluid){
  if(!canPlaceLiquid(null,level,position,state,fluid.getType()))return false;
  // A modern rail is a SimpleWaterloggedBlock. Preserve old liquid destruction,
  // rather than silently retaining water or turning a non-waterlogged rail waterproof.
  if(!level.isClientSide()){
   if(fluid.getType().isSame(Fluids.LAVA))level.levelEvent(1501,position,0);
   else if(level instanceof Level world)dropResources(state,world,position);
   level.setBlock(position,fluid.createLegacyBlock(),3);
  }
  return true;
 }
 @Override protected FluidState getFluidState(BlockState state){return Fluids.EMPTY.defaultFluidState();}
 @Override public int getLightBlock(BlockState state,BlockGetter level,BlockPos position){return 0;}
 @Override public boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos position){return true;}
 public BlockState fromLegacy(int metadata){if(metadata<0||metadata>=LEGACY_SHAPES.length)throw new IllegalArgumentException("Invalid source rail metadata "+metadata);return defaultBlockState().setValue(SHAPE,LEGACY_SHAPES[metadata]);}
 private static final RailShape[]LEGACY_SHAPES={RailShape.NORTH_SOUTH,RailShape.EAST_WEST,RailShape.ASCENDING_EAST,RailShape.ASCENDING_WEST,RailShape.ASCENDING_NORTH,RailShape.ASCENDING_SOUTH,RailShape.SOUTH_EAST,RailShape.SOUTH_WEST,RailShape.NORTH_WEST,RailShape.NORTH_EAST};
}
