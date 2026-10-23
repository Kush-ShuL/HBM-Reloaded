package com.hbm.block.pipe;

import com.hbm.fluid.network.FluidNetworks;
import com.hbm.fluid.network.FluidUser;
import com.hbm.fluid.HbmFluidType;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.item.fluid.FluidIdentifierItem;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NativePipeBlock extends BaseEntityBlock implements com.hbm.api.block.IAnalyzable {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    private final boolean valve;
    private final MapCodec<NativePipeBlock> codec;
    public NativePipeBlock(boolean valve) {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 6F).sound(valve ? SoundType.STONE : PipeSounds.TYPE).noOcclusion().requiresCorrectToolForDrops());
        this.valve = valve; codec = simpleCodec(ignored -> new NativePipeBlock(valve));
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0).setValue(OPEN, false));
    }
    /** ExhaustPipeAnchor did not implement source IAnalyzable; native SpecialPipeBlock merges both anchor classes. */
    @Override public boolean supportsAnalysis() { return kind() != PipeKind.EXHAUST_ANCHOR; }
    /** Source FluidDuctBase/FluidPipeAnchor.getDuctDebugInfo; lookup never creates or refreshes a network. */
    @Override public List<String> getDebugInfo(Level level,BlockPos pos) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)
                || !(level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe) || pipe.kind().exhaust()) return null;
        var type = pipe.getFluidType();
        if (type == null) return null;
        var node = FluidNetworks.nodeAt(server,type,pos);
        if (node == null || node.network() == null) return null;
        var net = node.network();
        return List.of("Links: " + net.links.size(), "Subscribers: " + net.receiverEntries.size(),
                "Providers: " + net.providerEntries.size(), "Transfer: " + net.fluidTracker);
    }
    public boolean valve() { return valve; }
    public PipeKind kind() { return valve ? PipeKind.VALVE : PipeKind.STANDARD; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(VARIANT, OPEN); }
    @Override protected RenderShape getRenderShape(BlockState state) { return valve ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return kind().instrument() ? new InstrumentPipeBlockEntity(pos, state) : new NativePipeBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type, PipeContent.ENTITY.get(), NativePipeBlockEntity::tick); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        var held = player.getMainHandItem();
        if (held.getItem() instanceof FluidIdentifier identifier && level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe && !pipe.kind().exhaust()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            var flags = PipeControls.flags(player);
            if (flags.alt() && held.getItem() instanceof FluidIdentifierItem && identifier.getType(level, pos, held) != pipe.getFluidType()) {
                FluidIdentifierItem.setType(held, pipe.getFluidType(), true);
                level.playSound(null, player, net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, net.minecraft.sounds.SoundSource.PLAYERS, 0.25F, 0.75F);
                return InteractionResult.SUCCESS;
            }
            var type = identifier.getType(level, pos, held);
            if (flags.control() || player.isShiftKeyDown()) { changeType(level, pos, pipe.getFluidType(), type, 64); return InteractionResult.SUCCESS; }
            if (pipe.getFluidType() != type) { pipe.setType(type); return InteractionResult.SUCCESS; }
        }
        if (valve && !player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean open = !state.getValue(OPEN); level.setBlock(pos, state.setValue(OPEN, open), 2);
                level.playSound(null, pos, HbmSounds.get("block.reactorStart"), net.minecraft.sounds.SoundSource.BLOCKS, 1F, open ? 1F : 0.85F);
                if (level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe) pipe.updateState();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    public static void changeType(Level level, BlockPos position, HbmFluidType previous, HbmFluidType type, int remaining) {
        if (!(level.getBlockEntity(position) instanceof NativePipeBlockEntity pipe) || pipe.getFluidType() != previous || pipe.kind().exhaust() || previous == type) return;
        pipe.setType(type);
        if (remaining > 0) {
            if (pipe.kind().anchor()) {
                changeType(level, position.relative(pipe.back()), previous, type, remaining - 1);
                for (BlockPos remote : pipe.connected()) changeType(level, remote, previous, type, remaining - 1);
            } else for (var side : Direction.values()) changeType(level, position.relative(side), previous, type, remaining - 1);
        }
    }
    public static int mask(BlockGetter level, BlockPos position, HbmFluidType type) {
        int mask = 0;
        for (Direction side : Direction.values()) {
            BlockPos target = position.relative(side);
            if (level.getBlockState(target).getBlock() instanceof com.hbm.fluid.network.FluidConnectorBlock connector) {
                if (connector.canConnect(type, level, target, side.getOpposite())) mask |= bit(side);
            } else if (level.getBlockEntity(target) instanceof FluidUser user && user.canConnect(type, side.getOpposite())) mask |= bit(side);
            else if (level instanceof Level actual) {
                var user = actual.getCapability(FluidNetworks.NATIVE_FLUID, target, side.getOpposite());
                if (user != null && user.canConnect(type, side.getOpposite())) mask |= bit(side);
            }
        }
        return mask;
    }
    public static int bit(Direction direction) { return switch (direction) { case EAST -> 32; case WEST -> 16; case UP -> 8; case DOWN -> 4; case SOUTH -> 2; case NORTH -> 1; }; }
    private int mask(BlockGetter level, BlockPos pos) { return level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe ? mask(level, pos, pipe.getFluidType()) : 0; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (valve) return Shapes.block();
        int mask = mask(level, pos);
        if (mask == 0) return Shapes.block();
        if ((mask & 15) == 0) return box(0, 5, 5, 16, 11, 11);
        if ((mask & 51) == 0) return box(5, 0, 5, 11, 16, 11);
        if ((mask & 60) == 0) return box(5, 5, 0, 11, 11, 16);
        return box((mask & 16) != 0 ? 0 : 5, (mask & 4) != 0 ? 0 : 5, (mask & 1) != 0 ? 0 : 5,
                (mask & 32) != 0 ? 16 : 11, (mask & 8) != 0 ? 16 : 11, (mask & 2) != 0 ? 16 : 11);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (valve) return Shapes.block();
        int mask = mask(level, pos);
        if (mask != 0 && ((mask & 15) == 0 || (mask & 51) == 0 || (mask & 60) == 0)) return getShape(state, level, pos, context);
        VoxelShape result = mask == 0 ? Shapes.empty() : box(5, 5, 5, 11, 11, 11);
        for (var side : Direction.values()) if (mask == 0 || (mask & bit(side)) != 0) result = Shapes.or(result, switch (side) {
            case EAST -> box(11, 5, 5, 16, 11, 11); case WEST -> box(0, 5, 5, 5, 11, 11);
            case UP -> box(5, 11, 5, 11, 16, 11); case DOWN -> box(5, 0, 5, 11, 5, 11);
            case SOUTH -> box(5, 5, 11, 11, 11, 16); case NORTH -> box(5, 5, 0, 11, 11, 5);
        });
        return result;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) { return List.of(valve ? new ItemStack(PipeContent.VALVE_ITEM.get()) : PipeContent.ITEM.get().stack(state.getValue(VARIANT))); }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        if (valve) return new ItemStack(PipeContent.VALVE_ITEM.get());
        if (level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe) return com.hbm.item.fluid.FluidItemContent.stack(PipeContent.TYPED_ITEM.get(), pipe.getFluidType());
        return PipeContent.ITEM.get().stack(state.getValue(VARIANT));
    }
}
