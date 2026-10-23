package com.hbm.multiblock;

import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** X5846 directional parent chains: 0..5 dummy, 6..11 port, 12..15 core. */
public abstract class DummyableBlock extends BaseEntityBlock {
    public static final IntegerProperty LEGACY_META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final ThreadLocal<Integer> SAFE_REMOVAL = ThreadLocal.withInitial(() -> 0);
    protected DummyableBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(LEGACY_META, 12)); }
    public abstract int[] dimensions();
    public int offset() { return 0; }
    public int heightOffset() { return 0; }
    public static boolean isCore(BlockState state) { return state.getValue(LEGACY_META) >= 12; }
    public static boolean isPort(BlockState state) { int meta = state.getValue(LEGACY_META); return meta >= 6 && meta < 12; }
    public static Direction facing(BlockState state) { return Direction.from3DDataValue(state.getValue(LEGACY_META) - 10); }
    public static Direction placementFacing(BlockState state) { return isCore(state)?facing(state):Direction.from3DDataValue(state.getValue(LEGACY_META)%6); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LEGACY_META); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    // Proxy collision/interaction cells have no visual surface to shade or block light.
    @Override protected float getShadeBrightness(BlockState state,BlockGetter level,BlockPos pos){return 1F;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
    public static void safely(Runnable action) { int depth = SAFE_REMOVAL.get(); SAFE_REMOVAL.set(depth + 1); try { action.run(); } finally { SAFE_REMOVAL.set(depth); } }
    public static int[] rotate(int[] dim, Direction direction) {
        return switch (direction) {
            case NORTH -> new int[]{dim[0],dim[1],dim[3],dim[2],dim[5],dim[4]};
            case EAST -> new int[]{dim[0],dim[1],dim[5],dim[4],dim[2],dim[3]};
            case WEST -> new int[]{dim[0],dim[1],dim[4],dim[5],dim[3],dim[2]};
            default -> dim.clone();
        };
    }
    protected BlockPos coreForPlacement(BlockPlaceContext context) { return context.getClickedPos().above(heightOffset()).relative(context.getHorizontalDirection().getOpposite(), -offset()); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos core = coreForPlacement(context);
        int[] d = rotate(dimensions(), facing);
        for (BlockPos part : BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]), core.offset(d[5],d[0],d[3]))) {
            if (!context.getLevel().isInWorldBounds(part) || !context.getLevel().hasChunkAt(part)) return null;
            if (!part.equals(context.getClickedPos()) && !context.getLevel().getBlockState(part).canBeReplaced()) return null;
        }
        // Only predict a visible core where it will actually remain. Offset machines
        // start as an invisible proxy until their correctly positioned core arrives.
        return defaultBlockState().setValue(LEGACY_META, facing.get3DDataValue() + (core.equals(context.getClickedPos())?10:0));
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        Direction facing = placementFacing(state);
        BlockState coreState=state.setValue(LEGACY_META,facing.get3DDataValue()+10);
        BlockPos core = pos.above(heightOffset()).relative(facing, -offset());
        int[] d = rotate(dimensions(), facing);
        safely(() -> {
            if (!pos.equals(core)) level.removeBlock(pos, false);
            level.setBlock(core, coreState, UPDATE_ALL);
            for (BlockPos candidate : BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]), core.offset(d[5],d[0],d[3]))) {
                BlockPos part = candidate.immutable();
                if (part.equals(core)) continue;
                Direction away = part.getY() < core.getY() ? Direction.DOWN : part.getY() > core.getY() ? Direction.UP
                        : part.getX() < core.getX() ? Direction.WEST : part.getX() > core.getX() ? Direction.EAST
                        : part.getZ() < core.getZ() ? Direction.NORTH : Direction.SOUTH;
                level.setBlock(part, defaultBlockState().setValue(LEGACY_META, away.get3DDataValue()), UPDATE_ALL);
            }
        });
        if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) && level.getBlockEntity(core) instanceof NamedMultiblock named) named.setCustomName(stack.getHoverName());
    }
    /** Never loads a chunk; cycle detection replaces the old mutable singleton search list. */
    public BlockPos findCore(BlockGetter level, BlockPos start) {
        return findCore(level, start, null);
    }
    /** Reports an unavailable traversal cell separately from a genuinely invalid structure. */
    public BlockPos findCore(BlockGetter level, BlockPos start, java.util.function.Consumer<BlockPos> unavailable) {
        var visited = new HashSet<BlockPos>();
        BlockPos current = start;
        while (visited.size() <= 2000 && visited.add(current)) {
            if (level instanceof Level world && !world.hasChunkAt(current)) {
                if (unavailable != null) unavailable.accept(current);
                return null;
            }
            BlockState state = level.getBlockState(current);
            if (!state.is(this)) return null;
            int meta = state.getValue(LEGACY_META);
            if (meta >= 12) return current;
            current = current.relative(Direction.from3DDataValue(meta >= 6 ? meta - 6 : meta).getOpposite());
        }
        return null;
    }
    public void setPort(Level level, BlockPos pos, boolean enabled) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(this) || isCore(state) || isPort(state) == enabled) return;
        safely(() -> { level.removeBlockEntity(pos); level.setBlock(pos, state.setValue(LEGACY_META, state.getValue(LEGACY_META) + (enabled ? 6 : -6)), UPDATE_ALL); });
        if (enabled && level.getBlockEntity(pos) == null) level.setBlockEntity(newBlockEntity(pos, level.getBlockState(pos)));
        level.invalidateCapabilities(pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) {
        if (level.isClientSide || SAFE_REMOVAL.get() > 0 || isCore(state)) return;
        BlockPos parent = pos.relative(Direction.from3DDataValue(state.getValue(LEGACY_META) % 6).getOpposite());
        // Match the original chunk-border orphan guard without forcing neighboring chunks.
        if (level.hasChunksAt(pos.offset(-1,-1,-1), pos.offset(1,1,1)) && !level.getBlockState(parent).is(this)) level.removeBlock(pos, false);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockPos core = findCore(level, pos);
        if (core == null || !(level.getBlockEntity(core) instanceof MenuProvider menu)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player instanceof ServerPlayer server) server.openMenu(menu, buffer -> { if (buffer.writerIndex() == 0) buffer.writeBlockPos(core); });
        return InteractionResult.CONSUME;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof MultiblockTool tool && tool.isHandDrill(stack, player) && !isCore(state)) {
            if (!level.isClientSide) { safely(() -> level.removeBlock(pos, false)); tool.onPartRemoved(stack, player, hand); }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) { return List.of(new ItemStack(this)); }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return new ItemStack(this); }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!replacement.is(this)) {
            if (!isCore(state) && SAFE_REMOVAL.get() == 0) {
                BlockPos parent = pos.relative(Direction.from3DDataValue(state.getValue(LEGACY_META) % 6).getOpposite());
                if (level.hasChunkAt(parent) && level.getBlockState(parent).is(this)) level.removeBlock(parent, false);
            }
            // Port inventories delegate to the core; only the actual core owns item drops.
            if (isCore(state) && level.getBlockEntity(pos) instanceof Container inventory) Containers.dropContents(level, pos, inventory);
        }
        super.onRemove(state, level, pos, replacement, moving);
    }
}
