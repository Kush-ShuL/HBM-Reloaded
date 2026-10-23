package com.hbm.pneumatic.anchor;
import com.hbm.pneumatic.tube.PneumaticTubeContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
public final class PneumaticAnchorBlock extends BaseEntityBlock {
 public static final DirectionProperty FACING=DirectionProperty.create("facing");
 public PneumaticAnchorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(com.hbm.block.pipe.PipeSounds.TYPE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.UP));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}@Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getClickedFace());}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PneumaticAnchorBlockEntity(pos,state);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,PneumaticTubeContent.ANCHOR_ENTITY.get(),PneumaticAnchorBlockEntity::tick);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){var d=state.getValue(FACING).getOpposite();return Block.box(d==Direction.WEST?0:4,d==Direction.DOWN?0:4,d==Direction.NORTH?0:4,d==Direction.EAST?16:12,d==Direction.UP?16:12,d==Direction.SOUTH?16:12);}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}@Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter world,BlockPos pos){return Shapes.empty();}
 @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&world.getBlockEntity(pos) instanceof PneumaticAnchorBlockEntity anchor)anchor.disconnectAll();super.onRemove(state,world,pos,next,moving);}
}
