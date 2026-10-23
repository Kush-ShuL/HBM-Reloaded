package com.hbm.block.pipe.decorative;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

/** Original BlockPipe: full collision and authored OBJ visuals, with no tank or network node. */
public final class DecorativePipeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create("axis",Direction.Axis.class);
    private static final MapCodec<DecorativePipeBlock> CODEC = simpleCodec(ignored -> new DecorativePipeBlock());
    private static final SoundType GRATE = new SoundType(.5F,1F,SoundType.STONE.getBreakSound(),SoundType.STONE.getStepSound(),SoundType.STONE.getPlaceSound(),SoundType.STONE.getHitSound(),SoundType.STONE.getFallSound()) {
        @Override public net.minecraft.sounds.SoundEvent getStepSound() { return HbmSounds.get("step.metalBlock"); }
    };
    public DecorativePipeBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(2F,3F).sound(GRATE).noOcclusion().requiresCorrectToolForDrops()); registerDefaultState(stateDefinition.any().setValue(AXIS,Direction.Axis.Y).setValue(WATERLOGGED,false)); }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(AXIS,WATERLOGGED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(AXIS,context.getClickedFace().getAxis()).setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER); }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return (rotation==Rotation.CLOCKWISE_90||rotation==Rotation.COUNTERCLOCKWISE_90)&&state.getValue(AXIS)!=Direction.Axis.Y ? state.setValue(AXIS,state.getValue(AXIS)==Direction.Axis.X?Direction.Axis.Z:Direction.Axis.X):state; }

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor world,BlockPos pos,BlockPos neighborPos){
        if(state.getValue(WATERLOGGED))world.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(world));
        return super.updateShape(state,direction,neighbor,world,pos,neighborPos);
    }
}
