package com.hbm.fluid.network;
import com.hbm.fluid.HbmFluidType;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
/** Original IFluidConnectorBlockMK2: real stateless connector-block predicate, without a synthetic block entity. */
public interface FluidConnectorBlock { boolean canConnect(HbmFluidType type,BlockGetter world,BlockPos pos,Direction side); }
