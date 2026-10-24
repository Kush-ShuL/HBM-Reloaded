package com.hbm.block.woodfixtures46;

import com.hbm.machine.fluidtank.LegacyFauxLadder;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** BlockWoodStructure/BlockEnumMulti: ROOF, SCAFFOLD, CEILING, including source scaffold collision and faux ladder. */
public final class WoodStructureBlock extends Block {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final VoxelShape ROOF = box(0, 0, 0, 16, 3, 16), SCAFFOLD = box(1, 0, 1, 15, 16, 15), CEILING = box(0, 14, 0, 16, 16, 16);
    private static final VoxelShape TOP_SUPPORT = box(0, 15, 0, 16, 16, 16);
    public WoodStructureBlock() {
        super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(5, 9).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(META, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(META, metadata & 15); }
    public static int type(int metadata) { return Math.abs(metadata % 3); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return context.getItemInHand().getItem() instanceof WoodStructureItem item ? fromLegacy(item.type()) : defaultBlockState(); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (type(state.getValue(META))) { case 0 -> ROOF; case 1 -> Shapes.block(); default -> CEILING; };
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (type(state.getValue(META))) { case 0 -> ROOF; case 1 -> SCAFFOLD; default -> CEILING; };
    }
    @Override protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) { return type(state.getValue(META)) == 1 ? TOP_SUPPORT : Shapes.empty(); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) { return true; }
    @Override protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (type(state.getValue(META)) == 1 && entity instanceof Player player) LegacyFauxLadder.mark(player);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var drops = new ArrayList<>(super.getDrops(state, params));
        for(int i=0;i<drops.size();i++){
            var stack=drops.get(i);if(!(stack.getItem() instanceof WoodStructureItem))continue;
            var typed=WoodStructureItem.stack(type(state.getValue(META)));typed.setCount(stack.getCount());typed.applyComponents(stack.getComponentsPatch());drops.set(i,typed);
        }
        return drops;
    }
    @Override public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) { return WoodStructureItem.stack(type(state.getValue(META))); }
    // No rotation/mirror override: original NBTStructure does not transform this custom metadata.
}
