package com.hbm.machine.radiobox;

import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Source wall-side geometry, horizontal placement and exact +/-4 switch metadata. */
public final class RadioboxBlock extends BaseEntityBlock {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    public RadioboxBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(SoundType.STONE)
                .requiresCorrectToolForDrops().noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new RadioboxEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, RadioboxContent.ENTITY.get(), RadioboxEntity::tick);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(META, context.getHorizontalDirection().getOpposite().get3DDataValue());
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(META)) {
            case 4, 8 -> box(11, 1, 4, 16, 15, 12);
            case 2, 6 -> box(4, 1, 11, 12, 15, 16);
            case 5, 9 -> box(0, 1, 4, 5, 15, 12);
            case 3, 7 -> box(4, 1, 0, 12, 15, 5);
            default -> box(0, 0, 2, 16, 16, 14);
        };
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }
    private static BlockState transform(BlockState state, java.util.function.UnaryOperator<Direction> rotation) {
        int meta = state.getValue(META), direction = meta >= 6 && meta <= 9 ? meta - 4 : meta;
        if (direction < 2 || direction > 5) return state;
        return state.setValue(META, rotation.apply(Direction.from3DDataValue(direction)).get3DDataValue() + (meta > 5 ? 4 : 0));
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return transform(state, rotation::rotate); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return transform(state, mirror::mirror); }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // Preserve the source client success and server-side non-sneak control boundary.
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown() || !(level.getBlockEntity(pos) instanceof RadioboxEntity box)) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        if (held.is(HbmContent.item("battery_spark")) && !box.infinite) {
            held.shrink(1); // Source also consumes one in creative mode.
            level.playSound(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    HbmSounds.get("item.upgradePlug"), SoundSource.BLOCKS, 1.5F, 1F);
            box.infinite = true; box.setChanged();
            return InteractionResult.CONSUME;
        }
        int meta = state.getValue(META);
        level.setBlock(pos, state.setValue(META, meta <= 5 ? meta + 4 : meta - 4), Block.UPDATE_CLIENTS);
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), HbmSounds.get("block.reactorStart"),
                SoundSource.BLOCKS, 1F, meta <= 5 ? 1F : .85F);
        return InteractionResult.CONSUME;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!next.is(this) && !level.isClientSide && level.getBlockEntity(pos) instanceof RadioboxEntity box && box.infinite)
            level.addFreshEntity(new ItemEntity(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    new ItemStack(HbmContent.item("battery_spark"))));
        super.onRemove(state, level, pos, next, moving);
    }
}
