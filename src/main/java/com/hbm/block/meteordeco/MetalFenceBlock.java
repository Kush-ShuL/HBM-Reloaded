package com.hbm.block.meteordeco;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
/** Original BlockMetalFence connectivity, one-block collision and subtype1 forced post. */
public final class MetalFenceBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,1);public static final BooleanProperty NORTH=BlockStateProperties.NORTH,EAST=BlockStateProperties.EAST,SOUTH=BlockStateProperties.SOUTH,WEST=BlockStateProperties.WEST;
 public MetalFenceBlock(){super(Properties.of().strength(15,.15F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops().dynamicShape());registerDefaultState(stateDefinition.any().setValue(META,0).setValue(NORTH,false).setValue(EAST,false).setValue(SOUTH,false).setValue(WEST,false).setValue(WATERLOGGED,false));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,NORTH,EAST,SOUTH,WEST,WATERLOGGED);}
 public static BooleanProperty property(Direction side){return switch(side){case NORTH->NORTH;case EAST->EAST;case SOUTH->SOUTH;case WEST->WEST;default->throw new IllegalArgumentException("Horizontal side required");};}
 public boolean connects(BlockGetter level,BlockPos pos){var other=level.getBlockState(pos);return other.is(this)||other.getBlock() instanceof FenceGateBlock||other.isSolidRender(level,pos)&&!other.is(Blocks.PUMPKIN)&&!other.is(Blocks.CARVED_PUMPKIN)&&!other.is(Blocks.JACK_O_LANTERN)&&!other.is(Blocks.MELON);}
 public BlockState refresh(BlockState state,BlockGetter level,BlockPos pos){for(var side:Direction.Plane.HORIZONTAL)state=state.setValue(property(side),connects(level,pos.relative(side)));return state;}
 public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,Math.floorMod(meta,2));}public ItemStack stack(int meta,int count){var result=new ItemStack(this,count);result.set(HbmHazards.LEGACY_METADATA.get(),Math.floorMod(meta,2));return result;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return refresh(fromLegacy(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)).setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER),context.getLevel(),context.getClickedPos());}
 @Override protected BlockState updateShape(BlockState state,Direction side,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos from){if(state.getValue(WATERLOGGED))level.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(level));return side.getAxis().isHorizontal()?state.setValue(property(side),connects(level,from)):state;}
 public static boolean hasPost(BlockState state){if(state.getValue(META)==1)return true;boolean n=state.getValue(NORTH),s=state.getValue(SOUTH),e=state.getValue(EAST),w=state.getValue(WEST);return !(!n&&!s&&e&&w)&&!(!e&&!w&&n&&s);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){state=refresh(state,level,pos);boolean n=state.getValue(NORTH),s=state.getValue(SOUTH),w=state.getValue(WEST),e=state.getValue(EAST);VoxelShape shape=Shapes.empty();if(n||s)shape=Shapes.or(shape,Shapes.box(.375,0,n?0:.375,.625,1,s?1:.625));if(w||e||!n&&!s)shape=Shapes.or(shape,Shapes.box(w?0:.375,0,.375,e?1:.625,1,.625));return shape;}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){state=refresh(state,level,pos);return Shapes.box(state.getValue(WEST)?0:.375,0,state.getValue(NORTH)?0:.375,state.getValue(EAST)?1:.625,1,state.getValue(SOUTH)?1:.625);}
 // The original nonopaque fence has zero light opacity. Lighting must never
 // evaluate the live neighbor-dependent selection box: a lighting worker could
 // request FULL for the same chunk whose lighting completion it is producing.
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return state.getValue(WATERLOGGED)?1:0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return !state.getValue(WATERLOGGED);}
 @Override protected boolean isPathfindable(BlockState state,PathComputationType type){return false;}@Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of(stack(state.getValue(META),1));}@Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return stack(state.getValue(META),1);}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){var result=state;for(var direction:Direction.Plane.HORIZONTAL)result=result.setValue(property(rotation.rotate(direction)),state.getValue(property(direction)));return result;}@Override protected BlockState mirror(BlockState state,Mirror mirror){var result=state;for(var direction:Direction.Plane.HORIZONTAL)result=result.setValue(property(mirror.mirror(direction)),state.getValue(property(direction)));return result;}

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }
}
