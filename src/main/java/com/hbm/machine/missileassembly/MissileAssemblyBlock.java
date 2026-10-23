package com.hbm.machine.missileassembly;

import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original single full-collision cube. The wide assembly model/struts are visual, not fake multiblock proxies. */
public final class MissileAssemblyBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final Random dropRandom = new Random();
    public MissileAssemblyBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5, 6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()); registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH)); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(ignored -> new MissileAssemblyBlock()); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(FACING, metadata >= 2 && metadata <= 5 ? Direction.from3DDataValue(metadata) : Direction.SOUTH); }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MissileAssemblyBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type, MissileAssemblyContent.ENTITY.get(), MissileAssemblyBlockEntity::tick); }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof MissileAssemblyBlockEntity machine) machine.setCustomName(stack.getHoverName());
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        // Existing actual BossSpawnHandler mark contract, shared with source nuclear machines.
        var root = player.getPersistentData(); var persisted = root.getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putLong("fbiMark", level.getGameTime() + 24000); root.put(Player.PERSISTED_NBT_TAG, persisted);
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof MissileAssemblyBlockEntity machine) server.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
        return InteractionResult.CONSUME;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof MissileAssemblyBlockEntity machine) {
            if (!level.isClientSide) for (int slot = 0; slot < machine.getContainerSize(); slot++) {
                ItemStack stack = machine.getItem(slot); if (stack.isEmpty()) continue;
                float x = dropRandom.nextFloat() * .8F + .1F, y = dropRandom.nextFloat() * .8F + .1F, z = dropRandom.nextFloat() * .8F + .1F;
                while (!stack.isEmpty()) {
                    int count = Math.min(dropRandom.nextInt(21) + 10, stack.getCount());
                    var item = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, stack.split(count));
                    item.setDeltaMovement((float) dropRandom.nextGaussian() * .05F, (float) dropRandom.nextGaussian() * .05F + .2F, (float) dropRandom.nextGaussian() * .05F);
                    level.addFreshEntity(item);
                }
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
