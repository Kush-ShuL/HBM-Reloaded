package com.hbm.block.fluid;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidType;

/** Native FluidState bridge; all spreading still runs the original block algorithm. */
public final class LegacyWorldFluid extends Fluid {
    private final WorldFluidSpec spec;
    private final Supplier<FluidType> type;
    private final Supplier<LegacyFluidBlock> block;
    private Supplier<? extends Item> bucket = () -> Items.AIR;
    public LegacyWorldFluid(WorldFluidSpec spec, Supplier<FluidType> type, Supplier<LegacyFluidBlock> block) {
        this.spec = spec; this.type = type; this.block = block;
        registerDefaultState(stateDefinition.any().setValue(LegacyFluidBlock.LEVEL, 0));
    }
    public void bucket(Supplier<? extends Item> bucket) { this.bucket = bucket; }
    @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) { builder.add(LegacyFluidBlock.LEVEL); }
    @Override public FluidType getFluidType() { return type.get(); }
    public Item nativeBucket() { return bucket.get(); }
    @Override public Item getBucket() { return com.hbm.fluid.FluidInteropConfig.enabled() ? nativeBucket() : Items.AIR; }
    @Override protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) { return false; }
    @Override protected Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) { return block.get().getFlowVector(new LegacyFluidWorld(level), pos.getX(), pos.getY(), pos.getZ()); }
    @Override public int getTickDelay(LevelReader level) { return spec.publicTickRate(); }
    @Override protected float getExplosionResistance() { return 300F; }
    @Override public float getOwnHeight(FluidState state) {
        int metadata = state.getValue(LegacyFluidBlock.LEVEL);
        int quanta = spec.finite() ? metadata + 1 : spec.quanta() - metadata;
        return Math.max(0, Math.min(1F, (quanta + 1F) / spec.quanta()));
    }
    @Override public float getHeight(FluidState state, BlockGetter level, BlockPos pos) { return getOwnHeight(state); }
    @Override protected BlockState createLegacyBlock(FluidState state) { return block.get().defaultBlockState().setValue(LegacyFluidBlock.LEVEL, state.getValue(LegacyFluidBlock.LEVEL)); }
    @Override public boolean isSource(FluidState state) { return state.getValue(LegacyFluidBlock.LEVEL) == (spec.finite() ? spec.quanta() - 1 : 0); }
    @Override public int getAmount(FluidState state) { return Math.max(0, spec.finite() ? state.getValue(LegacyFluidBlock.LEVEL) + 1 : spec.quanta() - state.getValue(LegacyFluidBlock.LEVEL)); }
    @Override public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) { return isSource(state) ? Shapes.block() : Shapes.empty(); }
}
