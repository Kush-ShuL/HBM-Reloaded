package com.hbm.armor.neutrinolens;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

/** Real BlockPinkLog/BlockLog metadata: oak bark,pink cap,three axes and source bark-only12..15. */
public final class PinkLogBlock extends RotatedPillarBlock {
    public static final MapCodec<PinkLogBlock> CODEC = simpleCodec(ignored -> new PinkLogBlock());
    public static final BooleanProperty BARK = BooleanProperty.create("bark");
    public static final IntegerProperty VARIANT = IntegerProperty.create("legacy_variant", 0, 3);
    public PinkLogBlock() {
        super(Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(MapColor.WOOD).strength(.5F, .5F).sound(SoundType.WOOD));
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(BARK, false).setValue(VARIANT, 0));
    }
    @Override public MapCodec<? extends RotatedPillarBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(BARK, VARIANT); }
    public BlockState fromLegacy(int metadata) {
        if (metadata < 0 || metadata > 15) throw new IllegalArgumentException("Invalid source pink log metadata " + metadata);
        return defaultBlockState().setValue(VARIANT, metadata & 3).setValue(BARK, (metadata & 12) == 12)
                .setValue(AXIS, (metadata & 12) == 4 ? Direction.Axis.X : (metadata & 12) == 8 ? Direction.Axis.Z : Direction.Axis.Y);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Source placing overwrites old axis/bark with clicked-face axis, keeping only low2 species bits.
        return super.getStateForPlacement(context).setValue(BARK, false)
                .setValue(VARIANT, context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) & 3);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        var stack = new ItemStack(this);
        if (state.getValue(VARIANT) != 0) stack.set(HbmHazards.LEGACY_METADATA.get(), state.getValue(VARIANT));
        return stack;
    }
}
