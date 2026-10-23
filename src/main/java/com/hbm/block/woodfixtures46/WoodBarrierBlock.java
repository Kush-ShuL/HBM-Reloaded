package com.hbm.block.woodfixtures46;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** BlockBarrier: selected side is thin, while each attached side contributes its own collision wall. */
public final class WoodBarrierBlock extends Block {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final Direction[] JOIN_SIDES = {Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.UP};
    private static final VoxelShape WEST = box(0, 0, 0, 2, 16, 16), NORTH = box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape EAST = box(14, 0, 0, 16, 16, 16), SOUTH = box(0, 0, 14, 16, 16, 16);

    public WoodBarrierBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(5, 9).noOcclusion().dynamicShape());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(META, metadata & 15); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        // The original horizontal onBlockPlaced side wins over the later yaw callback.
        if (context.getClickedFace().getAxis().isHorizontal()) return fromLegacy(context.getClickedFace().get3DDataValue());
        int yaw = Mth.floor(context.getRotation() * 4F / 360F + .5D) & 3;
        return fromLegacy(new int[]{2, 5, 3, 4}[yaw]);
    }
    /** Modern full opaque render cubes are the source opaque/normal-cube attachment counterpart. */
    private static boolean solid(BlockGetter world, BlockPos pos) { return world.getBlockState(pos).isSolidRender(world, pos); }
    public static int joins(BlockGetter world, BlockPos pos, BlockState state) {
        int key = 0, metadata = state.getValue(META);
        int[] forced = {5, 3, 4, 2, -1};
        for (int i = 0; i < JOIN_SIDES.length; i++) if (metadata == forced[i] || solid(world, pos.relative(JOIN_SIDES[i]))) key |= 1 << i;
        return key;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(META)) { case 5 -> WEST; case 3 -> NORTH; case 4 -> EAST; case 2 -> SOUTH; default -> Shapes.block(); };
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int key = joins(world, pos, state); VoxelShape result = Shapes.empty();
        if ((key & 1) != 0) result = Shapes.or(result, WEST);
        if ((key & 2) != 0) result = Shapes.or(result, NORTH);
        if ((key & 4) != 0) result = Shapes.or(result, EAST);
        if ((key & 8) != 0) result = Shapes.or(result, SOUTH);
        return result;
    }
    @Override protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    // The source has no INBTBlockTransformable/vanilla-directional branch: template metadata stays unchanged.
}
