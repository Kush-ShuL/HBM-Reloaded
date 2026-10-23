package com.hbm.block.cable;

import com.hbm.energy.HbmEnergy;
import com.hbm.energy.EnergyCapabilityProvider;
import com.hbm.energy.EnergyConnector;
import com.hbm.energy.SidedEnergyConnector;
import com.mojang.serialization.MapCodec;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NativeCableBlock extends BaseEntityBlock {
    public static final Map<Direction, BooleanProperty> CONNECTIONS = new EnumMap<>(Direction.class);
    static { for (Direction side : Direction.values()) CONNECTIONS.put(side, BooleanProperty.create(side.getName())); }
    private final CableKind kind;
    private final MapCodec<NativeCableBlock> codec;

    public NativeCableBlock(CableKind kind) {
        super(properties(kind));
        this.kind = kind;
        codec = simpleCodec(ignored -> new NativeCableBlock(kind));
        BlockState state = stateDefinition.any();
        for (var property : CONNECTIONS.values()) state = state.setValue(property, false);
        registerDefaultState(state);
    }
    private static Properties properties(CableKind kind) {
        Properties props = Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(SoundType.STONE).requiresCorrectToolForDrops();
        return kind.shaped() || kind == CableKind.DIODE ? props.noOcclusion() : props;
    }
    public CableKind kind() { return kind; }
    @Override protected MapCodec<? extends NativeCableBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        CONNECTIONS.values().forEach(builder::add);
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return kind == CableKind.GAUGE ? new GaugeCableBlockEntity(pos, state) : new NativeCableBlockEntity(pos, state);
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        if (kind == CableKind.GAUGE) return createTickerHelper(type, CableContent.GAUGE_ENTITY.get(), GaugeCableBlockEntity::tick);
        return createTickerHelper(type, CableContent.ENTITY.get(), NativeCableBlockEntity::tick);
    }
    public static boolean connected(BlockState state, Direction side) { return state.getValue(CONNECTIONS.get(side)); }
    public static boolean canConnectTo(BlockGetter level, BlockPos target, Direction fromCable) {
        if (target.getY() < level.getMinBuildHeight() || target.getY() >= level.getMaxBuildHeight()) return false;
        if (level instanceof Level world && !world.hasChunkAt(target)) return false;
        Direction face = fromCable.getOpposite();
        if (level.getBlockState(target).getBlock() instanceof api.hbm.energymk2.IEnergyConnectorBlock visual) return visual.canConnect(level,target,face);
        BlockEntity entity = level.getBlockEntity(target);
        EnergyConnector connector = entity instanceof EnergyCapabilityProvider provider ? provider.energyConnector(face)
                : entity instanceof EnergyConnector participant ? participant : null;
        // A render-region is not a Level and cannot query capabilities. Resolve the
        // same native participant and queried machine face as HbmEnergy.HE here.
        if (connector != null) return connector.isLoaded()
                && (!(connector instanceof SidedEnergyConnector sided) || sided.canConnect(face));
        if (entity instanceof EnergyCapabilityProvider) return false;
        return level instanceof Level world && world.getCapability(HbmEnergy.HE, target, fromCable.getOpposite()) != null;
    }
    public BlockState refreshConnections(BlockState state, BlockGetter level, BlockPos pos) {
        for (Direction side : Direction.values()) state = state.setValue(CONNECTIONS.get(side), canConnectTo(level, pos.relative(side), side));
        return state;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return refreshConnections(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }
    @Override protected BlockState updateShape(BlockState state, Direction side, BlockState adjacent, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
        return state.setValue(CONNECTIONS.get(side), canConnectTo(level, adjacentPos, side));
    }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState previous, boolean moving) {
        super.onPlace(state, level, pos, previous, moving);
        if (!level.isClientSide && !previous.is(this) && level.getBlockEntity(pos) instanceof NativeCableBlockEntity cable) cable.refreshConnections();
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof NativeCableBlockEntity cable) cable.disconnect();
        super.onRemove(state, level, pos, replacement, moving);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(stackFor(state));
    }
    protected ItemStack stackFor(BlockState state) {
        CableBlockItem item = CableContent.ITEMS_BY_KIND.get(kind).get();
        return kind == CableKind.BOX ? item.stack(state.getValue(BoxCableBlock.SIZE)) : new ItemStack(item);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return stackFor(state); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!kind.shaped()) return Shapes.block();
        double lower = kind == CableKind.BOX ? 2 + state.getValue(BoxCableBlock.SIZE) : 5.5;
        double upper = 16 - lower;
        int count = 0;
        for (Direction side : Direction.values()) if (connected(state, side)) count++;
        if (kind == CableKind.BOX && count > 0) {
            if (axisOnly(state, Direction.Axis.X)) return box(0, lower, lower, 16, upper, upper);
            if (axisOnly(state, Direction.Axis.Y)) return box(lower, 0, lower, upper, 16, upper);
            if (axisOnly(state, Direction.Axis.Z)) return box(lower, lower, 0, upper, upper, 16);
        }
        return box(connected(state, Direction.WEST) ? 0 : lower, connected(state, Direction.DOWN) ? 0 : lower,
                connected(state, Direction.NORTH) ? 0 : lower, connected(state, Direction.EAST) ? 16 : upper,
                connected(state, Direction.UP) ? 16 : upper, connected(state, Direction.SOUTH) ? 16 : upper);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (kind != CableKind.BOX) return getShape(state, level, pos, context);
        double lower = 2 + state.getValue(BoxCableBlock.SIZE), upper = 16 - lower;
        if (axisOnly(state, Direction.Axis.X) || axisOnly(state, Direction.Axis.Y) || axisOnly(state, Direction.Axis.Z)) return getShape(state, level, pos, context);
        VoxelShape result = box(lower, lower, lower, upper, upper, upper);
        for (Direction side : Direction.values()) if (connected(state, side)) result = Shapes.or(result, arm(side, lower, upper));
        return result;
    }
    public static boolean axisOnly(BlockState state, Direction.Axis axis) {
        boolean connected = false;
        for (Direction side : Direction.values()) if (connected(state, side)) {
            if (side.getAxis() != axis) return false;
            connected = true;
        }
        return connected;
    }
    private static VoxelShape arm(Direction side, double lower, double upper) {
        return switch (side) {
            case DOWN -> box(lower, 0, lower, upper, lower, upper);
            case UP -> box(lower, upper, lower, upper, 16, upper);
            case NORTH -> box(lower, lower, 0, upper, upper, lower);
            case SOUTH -> box(lower, lower, upper, upper, upper, 16);
            case WEST -> box(0, lower, lower, lower, upper, upper);
            case EAST -> box(upper, lower, lower, 16, upper, upper);
        };
    }
}
