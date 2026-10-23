package com.hbm.fluid.interop;

import java.util.function.Supplier;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.fluids.FluidType;

/** Optional transport identity. It creates no world block, bucket or alternative fluid storage. */
final class InteropFluid extends Fluid {
    private final Supplier<? extends FluidType> type;
    InteropFluid(Supplier<? extends FluidType> type){this.type=type;}
    @Override public FluidType getFluidType(){return type.get();}
    @Override public Item getBucket(){return Items.AIR;}
    @Override protected boolean canBeReplacedWith(FluidState state,BlockGetter level,BlockPos pos,Fluid other,Direction side){return false;}
    @Override protected Vec3 getFlow(BlockGetter level,BlockPos pos,FluidState state){return Vec3.ZERO;}
    @Override public int getTickDelay(LevelReader level){return 0;}
    @Override protected float getExplosionResistance(){return 0;}
    @Override public float getOwnHeight(FluidState state){return 0;}
    @Override public float getHeight(FluidState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected BlockState createLegacyBlock(FluidState state){return Blocks.AIR.defaultBlockState();}
    @Override public boolean isSource(FluidState state){return true;}
    @Override public int getAmount(FluidState state){return 0;}
    @Override public VoxelShape getShape(FluidState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
}
