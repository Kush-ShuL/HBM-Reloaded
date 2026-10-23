package com.hbm.block.sandbags;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** BlockSandbags is one neighbor-dependent cuboid, not a vanilla wall/fence or a directional block. */
public final class SandbagsBlock extends Block {
    public static final Direction[] SIDES = {Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH};
    private static final VoxelShape[] SHAPES = new VoxelShape[16];
    static { for (int key = 0; key < 16; key++) SHAPES[key] = Shapes.box((key & 1) != 0 ? 0 : .25, 0, (key & 2) != 0 ? 0 : .25, (key & 4) != 0 ? 1 : .75, 1, (key & 8) != 0 ? 1 : .75); }
    public SandbagsBlock() {
        // Original ground material is harvestable by hand; ModBlocks did not set a sound or harvest restriction.
        super(Properties.of().mapColor(MapColor.DIRT).strength(5, 18).noOcclusion().dynamicShape());
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    /** Same opaque/normal full-render-cube counterpart used by the actual wood-fixture adaptation. */
    public static boolean opaque(BlockGetter world, BlockPos pos) { return world.getBlockState(pos).isSolidRender(world, pos); }
    public static int joins(BlockGetter world, BlockPos pos, BlockState state) {
        int key = 0;
        for (int i = 0; i < SIDES.length; i++) {
            var neighbor = pos.relative(SIDES[i]);
            if (world.getBlockState(neighbor).is(state.getBlock()) || opaque(world, neighbor)) key |= 1 << i;
        }
        return key;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) { return SHAPES[joins(world, pos, state)]; }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) { return getShape(state, world, pos, context); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
}
