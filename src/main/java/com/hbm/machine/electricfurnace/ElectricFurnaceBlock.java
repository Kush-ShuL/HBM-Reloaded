package com.hbm.machine.electricfurnace;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;

public final class ElectricFurnaceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final MapCodec<ElectricFurnaceBlock> OFF_CODEC = simpleCodec(ignored -> new ElectricFurnaceBlock(false));
    private static final MapCodec<ElectricFurnaceBlock> ON_CODEC = simpleCodec(ignored -> new ElectricFurnaceBlock(true));
    private final boolean active;
    public ElectricFurnaceBlock(boolean active) {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> active ? 15 : 0));
        this.active = active;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }
    public boolean active() { return active; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return active ? ON_CODEC : OFF_CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(FACING))); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ElectricFurnaceBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type, ElectricFurnaceContent.ENTITY.get(), ElectricFurnaceBlockEntity::tick); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof ElectricFurnaceBlockEntity machine) server.openMenu(machine);
        return InteractionResult.CONSUME;
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof ElectricFurnaceBlockEntity machine) machine.setCustomName(stack.getHoverName());
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (replacement.getBlock() instanceof ElectricFurnaceBlock) return; // Both states share one valid block entity.
        if (!(replacement.getBlock() instanceof ElectricFurnaceBlock) && level.getBlockEntity(pos) instanceof ElectricFurnaceBlockEntity machine) {
            Containers.dropContents(level, pos, machine);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return new ItemStack(ElectricFurnaceContent.ITEM.get()); }
    @Override public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!active) return;
        Direction facing = state.getValue(FACING);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + random.nextFloat() * 6F / 16F;
        double z = pos.getZ() + 0.5;
        float offset = random.nextFloat() * 0.6F - 0.3F;
        x += facing.getAxis() == Direction.Axis.X ? facing.getStepX() * 0.52F : offset;
        z += facing.getAxis() == Direction.Axis.Z ? facing.getStepZ() * 0.52F : offset;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }
}
