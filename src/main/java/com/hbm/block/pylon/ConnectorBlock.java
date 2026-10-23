package com.hbm.block.pylon;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ConnectorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=BlockStateProperties.FACING;
    public final PylonKind kind;
    public ConnectorBlock(PylonKind kind) { super(Properties.of().strength(5,6).requiresCorrectToolForDrops().noOcclusion()); this.kind=kind; registerDefaultState(stateDefinition.any().setValue(FACING,Direction.DOWN)); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(properties->new ConnectorBlock(kind)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getClickedFace()); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) {
        Direction back=state.getValue(FACING).getOpposite(); boolean full=kind==PylonKind.SUPER_CONNECTOR;
        return Block.box(back==Direction.WEST||full&&back==Direction.EAST?0:5,back==Direction.DOWN||full&&back==Direction.UP?0:5,back==Direction.NORTH||full&&back==Direction.SOUTH?0:5,
                back==Direction.EAST||full&&back==Direction.WEST?16:11,back==Direction.UP||full&&back==Direction.DOWN?16:11,back==Direction.SOUTH||full&&back==Direction.NORTH?16:11);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new PylonBlockEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return level.isClientSide?null:createTickerHelper(type,PylonContent.ENTITY.get(),PylonBlockEntity::tick); }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) {
        if(player.isShiftKeyDown()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(level.isClientSide) return PylonBlockEntity.dyeColor(stack)!=0?ItemInteractionResult.SUCCESS:ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return level.getBlockEntity(pos) instanceof PylonBlockEntity pylon&&pylon.setColor(stack)?ItemInteractionResult.SUCCESS:ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) {
        if(!replacement.is(this)&&level.getBlockEntity(pos) instanceof PylonBlockEntity pylon) pylon.disconnectAll();
        super.onRemove(state,level,pos,replacement,moving);
    }
}
