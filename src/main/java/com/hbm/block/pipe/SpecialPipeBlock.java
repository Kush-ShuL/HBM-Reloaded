package com.hbm.block.pipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;

/** Box ducts, paintable full blocks, instruments and the original two fluid anchor kinds. */
public final class SpecialPipeBlock extends NativePipeBlock {
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 4);
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final BooleanProperty HIDE_OVERLAY = BooleanProperty.create("hide_overlay");
    private final PipeKind kind;
    private final MapCodec<SpecialPipeBlock> codec;
    public SpecialPipeBlock(PipeKind kind) {
        super(false);
        this.kind = kind; codec = simpleCodec(ignored -> new SpecialPipeBlock(kind));
        registerDefaultState(defaultBlockState().setValue(SIZE, 0).setValue(FACING, Direction.UP).setValue(HIDE_OVERLAY, false));
    }
    @Override public PipeKind kind() { return kind; }
    @Override protected MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() { return codec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(SIZE, FACING, HIDE_OVERLAY); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public net.minecraft.world.level.block.SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        return kind.box() || kind.anchor() ? PipeSounds.TYPE : net.minecraft.world.level.block.SoundType.STONE;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        if (kind.anchor()) return state.setValue(FACING, context.getClickedFace());
        if (kind == PipeKind.GAUGE && context.getPlayer() != null) {
            Player player = context.getPlayer(); var pos = context.getClickedPos();
            Direction face = player.getDirection().getOpposite();
            if (Math.abs(player.getX() - pos.getX()) < 2 && Math.abs(player.getZ() - pos.getZ()) < 2) {
                double eye = player.getY() + player.getEyeHeight();
                if (eye - pos.getY() > 2) face = Direction.UP;
                if (pos.getY() - eye > 0) face = Direction.DOWN;
            }
            return state.setValue(FACING, face);
        }
        return state;
    }
    public int metadata(BlockState state) { return kind.box() ? state.getValue(SIZE) * 3 + state.getValue(VARIANT) : 0; }
    public int mask(BlockGetter level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe)) return 0;
        int mask = 0;
        for (var type : pipe.networkTypes()) mask |= NativePipeBlock.mask(level, pos, type);
        return mask;
    }
    public static VoxelShape boxShape(int meta, int mask, boolean selection) {
        double lower = 2 + meta / 3, upper = 14 - meta / 3, jLower = lower - 1, jUpper = upper + 1;
        if (mask == 0) return box(jLower, jLower, jLower, jUpper, jUpper, jUpper);
        if ((mask & 15) == 0) return box(0, lower, lower, 16, upper, upper);
        if ((mask & 51) == 0) return box(lower, 0, lower, upper, 16, upper);
        if ((mask & 60) == 0) return box(lower, lower, 0, upper, upper, 16);
        double centerLow = Integer.bitCount(mask) == 2 ? lower : jLower, centerHigh = Integer.bitCount(mask) == 2 ? upper : jUpper;
        if (selection) return box((mask & 16) != 0 ? 0 : centerLow, (mask & 4) != 0 ? 0 : centerLow, (mask & 1) != 0 ? 0 : centerLow,
                (mask & 32) != 0 ? 16 : centerHigh, (mask & 8) != 0 ? 16 : centerHigh, (mask & 2) != 0 ? 16 : centerHigh);
        VoxelShape result = box(centerLow, centerLow, centerLow, centerHigh, centerHigh, centerHigh);
        for (Direction side : Direction.values()) if ((mask & bit(side)) != 0) result = Shapes.or(result, switch (side) {
            case EAST -> box(upper, lower, lower, 16, upper, upper); case WEST -> box(0, lower, lower, lower, upper, upper);
            case UP -> box(lower, upper, lower, upper, 16, upper); case DOWN -> box(lower, 0, lower, upper, lower, upper);
            case SOUTH -> box(lower, lower, upper, upper, upper, 16); case NORTH -> box(lower, lower, 0, upper, upper, lower);
        });
        return result;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (kind.box()) return boxShape(metadata(state), mask(level, pos), true);
        if (kind.anchor()) return anchorShape(state.getValue(FACING).getOpposite());
        return Shapes.block();
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return kind.box() ? boxShape(metadata(state), mask(level, pos), false) : getShape(state, level, pos, context);
    }
    private static VoxelShape anchorShape(Direction back) {
        return box(back == Direction.WEST ? 0 : 4, back == Direction.DOWN ? 0 : 4, back == Direction.NORTH ? 0 : 4,
                back == Direction.EAST ? 16 : 12, back == Direction.UP ? 16 : 12, back == Direction.SOUTH ? 16 : 12);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!kind.paintable() || !(level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id.getNamespace().equals("hbm")) {
            boolean screwdriver = id.getPath().equals("screwdriver") || id.getPath().equals("screwdriver_desh");
            if (screwdriver && pipe.paint() != null || id.getPath().equals("defuser")) {
                if (!level.isClientSide) {
                    if (screwdriver) pipe.setPaint(null); else level.setBlock(pos, state.cycle(HIDE_OVERLAY), Block.UPDATE_ALL);
                    if (stack.isDamageableItem()) stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if (pipe.paint() == null && stack.getItem() instanceof BlockItem item) {
            BlockState paint = item.getBlock().defaultBlockState();
            var properties = stack.get(DataComponents.BLOCK_STATE); if (properties != null) paint = properties.apply(paint);
            if (!paint.is(Blocks.GRASS_BLOCK) && !paint.is(this) && paint.getRenderShape() == RenderShape.MODEL && Block.isShapeFullBlock(paint.getShape(level, pos))) {
                if (!level.isClientSide) pipe.setPaint(paint);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!state.is(replacement.getBlock()) && level.getBlockEntity(pos) instanceof NativePipeBlockEntity pipe && kind.anchor()) pipe.disconnectAll();
        super.onRemove(state, level, pos, replacement, moving);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) { return List.of(PipeContent.stack(this, metadata(state))); }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return PipeContent.stack(this, metadata(state)); }
}
