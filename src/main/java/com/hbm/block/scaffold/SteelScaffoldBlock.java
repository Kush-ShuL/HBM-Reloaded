package com.hbm.block.scaffold;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

/** Original BlockScaffold's four colors and four placement groups, with a single final placement state. */
public final class SteelScaffoldBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final VoxelShape ALONG_X = box(2, 0, 0, 14, 16, 16), ALONG_Y = box(0, 2, 0, 16, 14, 16), ALONG_Z = box(0, 0, 2, 16, 16, 14);
    public SteelScaffoldBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(5F, 9F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(META, 0).setValue(WATERLOGGED,false));
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(META,WATERLOGGED); }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(META, metadata & 15); }
    public static int color(int metadata) { return Math.abs(metadata % 4); }
    public ItemStack stack(int metadata, int count) {
        ItemStack stack = new ItemStack(this, count); stack.set(HbmHazards.LEGACY_METADATA.get(), color(metadata)); return stack;
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        int metadata = context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0);
        var face = context.getClickedFace();
        if (face.getAxis().isVertical()) {
            float yaw = context.getPlayer() == null ? 0F : context.getPlayer().getYRot();
            int rotation = Mth.floor((double)(yaw * 4F / 360F) + .5D) & 3;
            if (rotation % 2 != 0) metadata += 8;
        } else if (face.getAxis() == net.minecraft.core.Direction.Axis.Z) metadata += 4;
        else metadata += 12;
        return fromLegacy(metadata).setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int metadata = state.getValue(META);
        return metadata >= 12 ? ALONG_Y : metadata >= 8 ? ALONG_X : metadata >= 4 ? ALONG_Y : ALONG_Z;
    }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return getShape(state, level, pos, context); }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) { return List.of(stack(state.getValue(META), 1)); }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) { return stack(state.getValue(META), 1); }
    // No rotation/mirror override: source NBTStructure does not rotate BlockScaffold metadata.

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor world,BlockPos pos,BlockPos neighborPos){
        if(state.getValue(WATERLOGGED))world.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(world));
        return super.updateShape(state,direction,neighbor,world,pos,neighborPos);
    }
}
