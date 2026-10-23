package com.hbm.machine.shredder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Source MachineShredder is a full cube with fixed north/south front faces. */
public final class ShredderBlock extends BaseEntityBlock {
    public static final MapCodec<ShredderBlock> CODEC = simpleCodec(ignored -> new ShredderBlock());
    private final java.util.Random dropRandom = new java.util.Random();
    public ShredderBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(SoundType.STONE).requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ShredderBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type, ShredderContent.ENTITY.get(), ShredderBlockEntity::tick); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof ShredderBlockEntity machine) server.openMenu(machine);
        return InteractionResult.CONSUME;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (state.getBlock() != replacement.getBlock() && level.getBlockEntity(pos) instanceof ShredderBlockEntity machine) {
            if (!level.isClientSide) for (int slot = 0; slot < machine.getContainerSize(); slot++) {
                ItemStack stack = machine.getItem(slot);
                if (stack.isEmpty()) continue;
                float x = dropRandom.nextFloat() * .8F + .1F, y = dropRandom.nextFloat() * .8F + .1F, z = dropRandom.nextFloat() * .8F + .1F;
                while (!stack.isEmpty()) {
                    ItemStack part = stack.split(Math.min(dropRandom.nextInt(21) + 10, stack.getCount()));
                    var entity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, part);
                    entity.setDeltaMovement((float) dropRandom.nextGaussian() * .05F, (float) dropRandom.nextGaussian() * .05F + .2F, (float) dropRandom.nextGaussian() * .05F);
                    level.addFreshEntity(entity);
                }
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
