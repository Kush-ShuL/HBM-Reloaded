package com.hbm.missile.custom.launcher;

import com.hbm.interfaces.IBomb;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

/** Actual source old IMultiblock launchers adapted to the one native directional parent-chain mechanism. */
public final class CustomLauncherBlock extends DummyableBlock implements IBomb {
    public final boolean large;
    private static final ThreadLocal<Boolean> ASSEMBLING = ThreadLocal.withInitial(() -> false);
    private static final Random LARGE_DROPS = new Random();
    private final Random smallDrops = new Random();
    public static boolean keepInventory;
    public CustomLauncherBlock(boolean large) { super(Properties.of().mapColor(MapColor.METAL).strength(5, 6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()); this.large = large; registerDefaultState(defaultBlockState().setValue(LEGACY_META, large ? 15 : 13)); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return simpleCodec(ignored -> new CustomLauncherBlock(large)); }
    @Override public int[] dimensions() { int r = large ? 4 : 1; return new int[]{0, 0, r, r, r, r}; }
    /** The source metadata represents actual scaffold side:0east/1west/2south/3north. */
    public static Direction sourceFacing(int orientation) { return switch (orientation) { case 1 -> Direction.WEST; case 2 -> Direction.SOUTH; case 3 -> Direction.NORTH; default -> Direction.EAST; }; }
    public static int orientation(BlockState state) { return switch (facing(state)) { case WEST -> 1; case SOUTH -> 2; case NORTH -> 3; default -> 0; }; }
    public BlockState fromLegacy(int metadata) { return large ? defaultBlockState().setValue(LEGACY_META, sourceFacing(metadata).get3DDataValue() + 10) : defaultBlockState(); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var level = context.getLevel(); var core = context.getClickedPos(); int r = large ? 4 : 1;
        for (int x = -r; x <= r; x++) for (int z = -r; z <= r; z++) {
            if (x == 0 && z == 0) continue;
            if (large && (x == 0 || z == 0)) continue; // Source large placement checks the off-axis cells only.
            var pos = core.offset(x, 0, z); if (!level.isInWorldBounds(pos) || !level.hasChunkAt(pos) || !level.getBlockState(pos).canBeReplaced()) return null;
        }
        return defaultBlockState().setValue(LEGACY_META, (large ? context.getHorizontalDirection().getCounterClockWise() : Direction.SOUTH).get3DDataValue() + 10);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) assemble(level, pos, large ? orientation(state) : 0);
    }
    public void assemble(Level level, BlockPos core, int orientation) {
        boolean previous = ASSEMBLING.get(); ASSEMBLING.set(true);
        try {
            safely(() -> {
                level.setBlock(core, fromLegacy(orientation), Block.UPDATE_ALL);
                if (large) {
                    var tower = switch (orientation) { case 1 -> core.offset(-3, 0, 0); case 2 -> core.offset(0, 0, 3); case 3 -> core.offset(0, 0, -3); default -> core.offset(3, 0, 0); };
                    for (int y = 1; y < 12; y++) level.setBlock(tower.above(y), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
                int radius = large ? 4 : 1;
                for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && z == 0) continue;
                    boolean port = large ? x != 0 && z != 0 || (orientation < 2 ? z == 0 : x == 0) : x != 0 && z != 0;
                    Direction away = x < 0 ? Direction.WEST : x > 0 ? Direction.EAST : z < 0 ? Direction.NORTH : Direction.SOUTH;
                    level.setBlock(core.offset(x, 0, z), defaultBlockState().setValue(LEGACY_META, away.get3DDataValue() + (port ? 6 : 0)), Block.UPDATE_ALL);
                }
            });
        } finally { ASSEMBLING.set(previous); }
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) {
        if (!isCore(state)) return super.rotate(state, rotation);
        return state.setValue(LEGACY_META, rotation.rotate(facing(state)).get3DDataValue() + 10);
    }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return isCore(state) ? state.rotate(mirror.getRotation(facing(state))) : super.mirror(state, mirror); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (isCore(state)) return large ? new LaunchTableEntity(pos, state) : new CompactLauncherEntity(pos, state);
        if (isPort(state)) return large ? new LaunchTablePort(pos, state) : new CustomLauncherPort(pos, state);
        return null;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!isCore(state)) return null;
        return large ? createTickerHelper(type, CustomLauncherContent.TABLE_ENTITY.get(), CustomLauncherEntity::tick)
                : createTickerHelper(type, CustomLauncherContent.COMPACT_ENTITY.get(), CustomLauncherEntity::tick);
    }
    private boolean solid(BlockState state) { return isPort(state) || large && isCore(state); }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return solid(state) ? Shapes.block() : Shapes.empty(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Original plate/core bounds are y1..y1. A minimal positive ray-pick surface keeps their source top-plane GUI interaction usable.
        return solid(state) ? Shapes.block() : Shapes.box(0, 1 - 1D / 65536, 0, 1, 1, 1);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) { return isCore(state) ? List.of(new ItemStack(large ? CustomLauncherContent.LARGE_CORE_ITEM.get() : CustomLauncherContent.CORE_ITEM.get())) : List.of(); }
    @Override public BombReturnCode explode(Level level, int x, int y, int z) {
        if (large && level.isClientSide) return BombReturnCode.UNDEFINED;
        // Source detonator only addresses the center, even though regular proxy interaction opens the real menu.
        if (!(level.getBlockEntity(new BlockPos(x, y, z)) instanceof CustomLauncherEntity machine) || !machine.canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;
        machine.launchFromDesignator(); return BombReturnCode.LAUNCHED;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!replacement.is(this) && !level.isClientSide && !ASSEMBLING.get()) {
            if (!isCore(state)) {
                // The chunk has already written the replacement at pos before onRemove; start from the old cell's parent.
                var parent = pos.relative(Direction.from3DDataValue(state.getValue(LEGACY_META) % 6).getOpposite());
                var core = findCore(level, parent); if (core != null) level.destroyBlock(core, true); // Actual DummyOldBase breaks/drops the one real core.
            } else if (level.getBlockEntity(pos) instanceof CustomLauncherEntity machine && !(large && keepInventory)) {
                var random = large ? LARGE_DROPS : smallDrops;
                for (int slot = 0; slot < 8; slot++) {
                    var stack = machine.getItem(slot); if (stack.isEmpty()) continue;
                    float x = random.nextFloat() * .8F + .1F, y = random.nextFloat() * .8F + .1F, z = random.nextFloat() * .8F + .1F;
                    while (!stack.isEmpty()) { int amount = Math.min(random.nextInt(21) + 10, stack.getCount()); var drop = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, stack.split(amount));
                        drop.setDeltaMovement((float) random.nextGaussian() * .05F, (float) random.nextGaussian() * .05F + .2F, (float) random.nextGaussian() * .05F); level.addFreshEntity(drop); }
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        if (!replacement.is(this) && large && keepInventory && isCore(state) && level.getBlockEntity(pos) instanceof CustomLauncherEntity machine) {
            ItemStack[] saved = new ItemStack[8];
            for (int slot = 0; slot < 8; slot++) { saved[slot] = machine.getItem(slot); machine.setItem(slot, ItemStack.EMPTY); }
            try { super.onRemove(state, level, pos, replacement, moving); }
            finally { for (int slot = 0; slot < 8; slot++) machine.setItem(slot, saved[slot]); }
        } else super.onRemove(state, level, pos, replacement, moving);
    }
}
