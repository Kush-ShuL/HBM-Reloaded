package com.hbm.armor.neutrinolens;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

/** Pink log has one item identity; source axis and all-bark geometry are native block states. */
public final class PinkLogBlock extends RotatedPillarBlock {
    public static final MapCodec<PinkLogBlock> CODEC = simpleCodec(ignored -> new PinkLogBlock());
    public static final BooleanProperty BARK = BooleanProperty.create("bark");
    public PinkLogBlock() {
        super(Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(MapColor.WOOD).strength(.5F, .5F).sound(SoundType.WOOD));
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(BARK, false));
    }
    @Override public MapCodec<? extends RotatedPillarBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { super.createBlockStateDefinition(builder); builder.add(BARK); }
    public BlockState fromLegacy(int metadata) {
        if (metadata < 0 || metadata > 15) throw new IllegalArgumentException("Invalid source pink log metadata " + metadata);
        return defaultBlockState().setValue(BARK, (metadata & 12) == 12)
                .setValue(AXIS, (metadata & 12) == 4 ? Direction.Axis.X : (metadata & 12) == 8 ? Direction.Axis.Z : Direction.Axis.Y);
    }
}
