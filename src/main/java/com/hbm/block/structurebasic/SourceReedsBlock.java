package com.hbm.block.structurebasic;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
/** Actual BlockReeds: a full selection cube, no collision, supported only by original vanilla water blocks. */
public final class SourceReedsBlock extends BaseEntityBlock {
 public SourceReedsBlock(){super(Properties.of().mapColor(MapColor.PLANT).strength(0).sound(SoundType.GRASS).noCollission().noOcclusion().pushReaction(PushReaction.DESTROY).isSuffocating((s,l,p)->false).isViewBlocking((s,l,p)->false));}
 @Override protected MapCodec<? extends SourceReedsBlock> codec(){return simpleCodec(ignored->new SourceReedsBlock());}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().canSurvive(context.getLevel(),context.getClickedPos())?defaultBlockState():null;}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return level.getBlockState(pos.below()).is(Blocks.WATER);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!state.canSurvive(level,pos))level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.block();}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ReedsRenderEntity(pos,state);}
}
