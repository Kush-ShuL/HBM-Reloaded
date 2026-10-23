package com.hbm.machine.reactor.rbmk.boiler;
import com.hbm.fluid.*;
import com.hbm.fluid.network.FluidConnectorBlock;
import net.minecraft.core.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.MapColor;
/** True source RBMKLoader has no BE or independent network: its owner supplies actual nearby ports. */
public final class RbmkLoaderBlock extends Block implements FluidConnectorBlock {
    public RbmkLoaderBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(50,36).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override public boolean canConnect(HbmFluidType type,BlockGetter world,BlockPos pos,Direction side){return side==Direction.UP?type.hasTrait(FluidTraits.Heatable.class):type.hasTrait(FluidTraits.Coolable.class)||type==HbmFluids.PERFLUOROMETHYL;}
}
