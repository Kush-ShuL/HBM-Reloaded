package com.hbm.block.cable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class PaintableCableBlock extends NativeCableBlock {
    public static final BooleanProperty HIDE_OVERLAY = BooleanProperty.create("hide_overlay");
    private static final MapCodec<PaintableCableBlock> CODEC = simpleCodec(ignored -> new PaintableCableBlock());
    public enum Tool { SCREWDRIVER, DEFUSER }
    public PaintableCableBlock() { super(CableKind.PAINTABLE); registerDefaultState(defaultBlockState().setValue(HIDE_OVERLAY, false)); }
    @Override protected MapCodec<PaintableCableBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(HIDE_OVERLAY);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                        Player player, InteractionHand hand, BlockHitResult hit) {
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Tool tool = null;
        if (id.getNamespace().equals("hbm")) tool = switch (id.getPath()) {
            case "screwdriver", "screwdriver_desh" -> Tool.SCREWDRIVER;
            case "defuser" -> Tool.DEFUSER;
            default -> null;
        };
        if (tool != null && applyTool(state, level, pos, tool)) {
            if (!level.isClientSide && stack.isDamageableItem()) stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof BlockItem item && level.getBlockEntity(pos) instanceof NativeCableBlockEntity cable && cable.paint() == null) {
            BlockState paint = item.getBlock().defaultBlockState();
            var properties = stack.get(DataComponents.BLOCK_STATE);
            if (properties != null) paint = properties.apply(paint);
            if (!paint.is(Blocks.GRASS_BLOCK) && !paint.is(this) && Block.isShapeFullBlock(paint.getShape(level, pos))) {
                if (!level.isClientSide) cable.setPaint(paint);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    /** Original IToolable semantics, exposed for the shared tooling system when it is migrated. */
    public boolean applyTool(BlockState state, Level level, BlockPos pos, Tool tool) {
        if (!(level.getBlockEntity(pos) instanceof NativeCableBlockEntity cable)) return false;
        if (tool == Tool.SCREWDRIVER) {
            if (cable.paint() == null) return false;
            if (!level.isClientSide) cable.setPaint(null);
            return true;
        }
        if (!level.isClientSide) level.setBlock(pos, state.cycle(HIDE_OVERLAY), Block.UPDATE_ALL);
        return true;
    }
}
