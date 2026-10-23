package com.hbm.world.structure.forest;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
/** Actual decorative antenna, with its real empty source BE and no invented satellite functionality. */
public final class PoleReceiverBlock extends BaseEntityBlock {
 public PoleReceiverBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,9).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(ForestFixtureBlock.META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(ignored->new PoleReceiverBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(ForestFixtureBlock.META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(ForestFixtureBlock.META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PoleReceiverBlockEntity(pos,state);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(ForestFixtureBlock.META);return meta>=2&&meta<=5?state.setValue(ForestFixtureBlock.META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(ForestFixtureBlock.META);return meta>=2&&meta<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
}
