package com.hbm.machine.reactor.rbmk.display;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;
/** Source MiniPanelBase3/4depth shape, actual all-side rendering and initial opposite-player metadata. */
public class RbmkMiniPanelBlock extends BaseEntityBlock{
    public RbmkMiniPanelBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(3,18).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(DummyableBlock.LEGACY_META,0));}
    @Override protected MapCodec<? extends RbmkMiniPanelBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(DummyableBlock.LEGACY_META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(DummyableBlock.LEGACY_META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return null;}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(DummyableBlock.LEGACY_META)){case 2->box(0,0,4,16,16,16);case 3->box(0,0,0,16,16,12);case 4->box(4,0,0,16,16,16);case 5->box(0,0,0,12,16,16);default->Shapes.block();};}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
}
