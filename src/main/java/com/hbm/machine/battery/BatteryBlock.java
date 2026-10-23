package com.hbm.machine.battery;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

public final class BatteryBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final BatteryTier tier;
    private final MapCodec<BatteryBlock> codec;

    public BatteryBlock(BatteryTier tier) {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(SoundType.STONE).requiresCorrectToolForDrops());
        this.tier = tier;
        codec = simpleCodec(ignored -> new BatteryBlock(tier));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    public BatteryTier tier() { return tier; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return codec; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(FACING))); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BatteryBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, BatteryMachineContent.ENTITY.get(), BatteryBlockEntity::tick);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (level.isClientSide || previous.is(this)) return;
        boolean north = level.getBlockState(pos.north()).isSolidRender(level, pos.north());
        boolean south = level.getBlockState(pos.south()).isSolidRender(level, pos.south());
        boolean west = level.getBlockState(pos.west()).isSolidRender(level, pos.west());
        boolean east = level.getBlockState(pos.east()).isSolidRender(level, pos.east());
        Direction facing = Direction.SOUTH;
        if (south && !north) facing = Direction.NORTH;
        if (west && !east) facing = Direction.EAST;
        if (east && !west) facing = Direction.WEST;
        level.setBlock(pos, state.setValue(FACING, facing), Block.UPDATE_CLIENTS);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof BatteryBlockEntity machine) server.openMenu(machine);
        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null) level.setBlock(pos, level.getBlockState(pos).setValue(FACING, placer.getDirection().getOpposite()), Block.UPDATE_CLIENTS);
        if (level.getBlockEntity(pos) instanceof BatteryBlockEntity battery) {
            battery.restorePersistent(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
            if (stack.has(DataComponents.CUSTOM_NAME)) battery.setCustomName(stack.getHoverName());
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack drop = new ItemStack(this);
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BatteryBlockEntity battery) {
            drop.set(DataComponents.CUSTOM_DATA, CustomData.of(battery.persistentData()));
            if (battery.customName() != null) drop.set(DataComponents.CUSTOM_NAME, battery.customName());
        }
        return List.of(drop);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (state.is(replacement.getBlock())) return;
        if (level.getBlockEntity(pos) instanceof BatteryBlockEntity battery) {
            // Inventory drops separately; the block item retains only storage state.
            Containers.dropContents(level, pos, battery);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BatteryBlockEntity battery ? battery.comparatorPower() : 0;
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return new ItemStack(this); }
}
