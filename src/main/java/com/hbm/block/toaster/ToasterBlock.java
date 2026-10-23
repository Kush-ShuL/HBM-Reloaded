package com.hbm.block.toaster;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original BlockDecoToaster: type*4+yaw, .325F-tall rotated collision, true three-subtype drops. */
public final class ToasterBlock extends Block {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final VoxelShape EVEN = Shapes.box(.25, 0, .375, .75, .325F, .625);
    private static final VoxelShape ODD = Shapes.box(.375, 0, .25, .625, .325F, .75);
    public ToasterBlock() {
        // ModBlocks leaves the source default stone sound; Material.iron requires a pickaxe, not an iron tier.
        super(Properties.of().mapColor(MapColor.METAL).strength(5, 6).noOcclusion().requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(META, metadata & 15); }
    public static int damageDropped(int metadata) { return Math.abs(metadata) % 12 / 4; }
    public static int transformMeta(int metadata, int clockwiseTurns) { return (metadata + (clockwiseTurns & 3)) % 4 | metadata / 4 * 4; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int yaw = Mth.floor(context.getRotation() * 4F / 360F + .5D) & 3;
        int metadata = context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0);
        return fromLegacy(metadata * 4 + yaw); // One final write, preserving the original item damage input.
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) { return state.getValue(META) % 2 == 0 ? EVEN : ODD; }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) { return getShape(state, world, pos, context); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        var drops = super.getDrops(state, context);
        for (var stack : drops) if (stack.is(asItem())) stack.set(HbmHazards.LEGACY_METADATA.get(), damageDropped(state.getValue(META)));
        return drops;
    }
    @Override public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) { return ToasterItem.stack(damageDropped(state.getValue(META))); }
    // Native template processor applies literal INBTBlockTransformable metadata before native placement.
}
