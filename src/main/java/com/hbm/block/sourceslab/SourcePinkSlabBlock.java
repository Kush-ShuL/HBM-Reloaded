package com.hbm.block.sourceslab;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** Source pink slabs use ordinary ItemBlock, so they do not acquire ItemSlab's combining operation. */
public final class SourcePinkSlabBlock extends SlabBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 7);
    private final boolean doubled;
    private final MapCodec<SourcePinkSlabBlock> sourceCodec;
    public SourcePinkSlabBlock(boolean doubled) {
        this(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(0).dynamicShape(), doubled);
    }
    private SourcePinkSlabBlock(Properties properties, boolean doubled) {
        super(properties);
        this.doubled = doubled;
        sourceCodec = simpleCodec(propertiesIn -> new SourcePinkSlabBlock(propertiesIn, doubled));
        registerDefaultState(defaultBlockState().setValue(VARIANT, 0).setValue(TYPE, doubled ? SlabType.DOUBLE : SlabType.BOTTOM).setValue(WATERLOGGED, false));
    }
    @Override public MapCodec<? extends SlabBlock> codec() { return sourceCodec; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); builder.add(VARIANT);
    }
    public BlockState fromLegacy(int metadata) {
        return defaultBlockState().setValue(VARIANT, metadata & 7).setValue(TYPE, doubled ? SlabType.DOUBLE : (metadata & 8) == 0 ? SlabType.BOTTOM : SlabType.TOP);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (doubled) return defaultBlockState();
        Direction face = context.getClickedFace();
        boolean top = face == Direction.DOWN || face != Direction.UP && context.getClickLocation().y - context.getClickedPos().getY() > .5;
        return defaultBlockState().setValue(TYPE, top ? SlabType.TOP : SlabType.BOTTOM)
            .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
    @Override protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) { return false; }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        ItemStack result = new ItemStack(SourceSlabsContent.PINK_ITEM.get(), doubled ? 2 : 1);
        result.set(HbmHazards.LEGACY_METADATA.get(), state.getValue(VARIANT));
        return List.of(result);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack result = new ItemStack(this);
        result.set(HbmHazards.LEGACY_METADATA.get(), state.getValue(VARIANT));
        return result;
    }
}
