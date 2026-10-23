package com.hbm.geiger;

import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original GeigerCounter block, retaining its asymmetric selection/collision bounds. */
public final class GeigerBlock extends BaseEntityBlock {
    private static final MapCodec<GeigerBlock> CODEC=simpleCodec(ignored->new GeigerBlock());
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public GeigerBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(15F,.15F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.SOUTH));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING); }
    // Source yaw0 gives meta3/SOUTH: the counter faces along the placer's yaw, unlike furnace blocks.
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getHorizontalDirection()); }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return state.setValue(FACING,rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { return rotate(state,mirror.getRotation(state.getValue(FACING))); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new GeigerBlockEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return level.isClientSide?null:createTickerHelper(type,GeigerContent.ENTITY.get(),GeigerBlockEntity::tick); }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        return switch(state.getValue(FACING)) {
            case WEST->box(0,0,0,14,9,14.5); case NORTH->box(1.5,0,0,16,9,14);
            case EAST->box(2,0,1.5,16,9,16); default->box(0,0,2,14.5,9,16);
        };
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if(level.isClientSide) return InteractionResult.SUCCESS;
        if(player.isShiftKeyDown()) return InteractionResult.PASS;
        level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBoop"),net.minecraft.sounds.SoundSource.PLAYERS,1,1);
        GeigerReadings.print(player); return InteractionResult.CONSUME;
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos) { return level.getBlockEntity(pos) instanceof GeigerBlockEntity geiger?GeigerClicks.comparator(geiger.check()):0; }
}
