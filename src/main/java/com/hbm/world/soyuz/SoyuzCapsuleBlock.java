package com.hbm.world.soyuz;

import com.hbm.machine.storage.CrateContents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Source ordinary BlockContainer: full one-block collision, nonopaque, no multiblock/proxy or yaw placement. */
public final class SoyuzCapsuleBlock extends BaseEntityBlock {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    public SoyuzCapsuleBlock() {
        super(Properties.of().strength(5, 6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends SoyuzCapsuleBlock> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(META); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SoyuzCapsuleBlockEntity(pos, state); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof SoyuzCapsuleBlockEntity capsule) server.openMenu(capsule, pos);
        return InteractionResult.CONSUME;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof SoyuzCapsuleBlockEntity capsule) {
            CrateContents.ejectWorld(level, pos.getX(), pos.getY(), pos.getZ(), capsule);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
