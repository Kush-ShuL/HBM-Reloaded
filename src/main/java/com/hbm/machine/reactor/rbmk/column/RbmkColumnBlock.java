package com.hbm.machine.reactor.rbmk.column;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.core.RbmkNeutronField;
import com.hbm.machine.reactor.rbmk.residue.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Source RBMKBase dynamic vertical parent chain; every cell has real visible geometry and source opacity. */
public abstract class RbmkColumnBlock extends DummyableBlock implements com.hbm.machine.reactor.rbmk.core.RbmkColumnBlock, RbmkLidTarget, ToolableBlock {
    public static boolean dropLids = true, digamma;
    protected RbmkColumnBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(3, 18).sound(SoundType.STONE).requiresCorrectToolForDrops().dynamicShape()); }
    @Override public int[] dimensions() { return new int[] {3, 0, 0, 0, 0, 0}; }
    public int[] dimensions(Level world) { return new int[] {RbmkDials.getColumnHeight(world), 0, 0, 0, 0, 0}; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var world = context.getLevel(); var pos = context.getClickedPos();
        for (int y = 1; y <= RbmkDials.getColumnHeight(world); y++) {
            var next = pos.above(y); if (!world.isInWorldBounds(next) || !world.hasChunkAt(next) || !world.getBlockState(next).canBeReplaced()) return null;
        }
        return defaultBlockState().setValue(LEGACY_META, 12); // Source getDirModified always NORTH/no lid.
    }
    @Override public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (world.isClientSide || !(placer instanceof Player)) return;
        int height = RbmkDials.getColumnHeight(world);
        safely(() -> {
            world.setBlock(pos, state.setValue(LEGACY_META, 12), UPDATE_ALL);
            for (int y = 1; y <= height; y++) world.setBlock(pos.above(y), defaultBlockState().setValue(LEGACY_META, y == height ? 7 : 1), UPDATE_ALL);
        });
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 15; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return false; }
    @Override protected float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) { return .2F; }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.block(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) { return Shapes.block(); }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        var core = core(world, pos); return core != null && core.hasLid() ? box(0, 0, 0, 16, 20, 16) : Shapes.block();
    }
    public RbmkColumnEntity core(BlockGetter world, BlockPos pos) { var core = findCore(world, pos); return core != null && world.getBlockEntity(core) instanceof RbmkColumnEntity machine ? machine : null; }
    @Override public RbmkLidTarget.Column rbmkCore(Level world, BlockPos pos) { return core(world, pos); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        var core = core(world, pos); if (core == null) return InteractionResult.PASS;
        if (player.getMainHandItem().getItem() instanceof RbmkLidItem && !core.hasLid()) return InteractionResult.PASS;
        if (!player.isShiftKeyDown() && core instanceof net.minecraft.world.MenuProvider) return super.useWithoutItem(state, world, pos, player, hit);
        return InteractionResult.sidedSuccess(world.isClientSide); // Source openInv succeeds even for passive columns without a GUI.
    }
    @Override public boolean onScrew(Level world, Player player, BlockPos pos, Direction side, float x, float y, float z, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;
        var core = core(world, pos);
        if (core == null || !core.hasLid() || !core.isLidRemovable()) return false;
        RbmkNeutronField.lid(world, core.getBlockPos(), false);
        if (!world.isClientSide) { dropLid(world, core.getBlockPos(), core.getBlockState()); core.removeLid(); }
        return true;
    }
    private static void dropLid(Level world, BlockPos core, BlockState state) {
        int meta = state.getValue(LEGACY_META);
        if (meta != 13 && meta != 15) return;
        var item = meta == 13 ? RbmkResidueContent.GLASS_LID : RbmkResidueContent.LID;
        world.addFreshEntity(new ItemEntity(world, core.getX() + .5, core.getY() + .5 + RbmkDials.getColumnHeight(world), core.getZ() + .5, new ItemStack(item.get())));
    }
    @Override protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState replacement, boolean moving) {
        if (!replacement.is(this) && !world.isClientSide && dropLids && isCore(state)) dropLid(world, pos, state);
        super.onRemove(state, world, pos, replacement, moving);
    }
}
