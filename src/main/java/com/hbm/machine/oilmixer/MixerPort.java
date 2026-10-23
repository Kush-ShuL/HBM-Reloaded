package com.hbm.machine.oilmixer;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidTransceiver;
import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** The actual source top-UP endpoint only; no added inventory or standard fluid/FE bridge. */
public final class MixerPort extends BlockEntity implements EnergyReceiver,SidedEnergyConnector,StandardFluidTransceiver,MachineView {
 public MixerPort(BlockPos pos,BlockState state){super(MixerContent.PORT.get(),pos,state);}@Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isPort(state);}public MixerEntity core(){if(level==null||!(getBlockState().getBlock() instanceof MixerBlock block))return null;var pos=block.findCore(level,worldPosition);return pos!=null&&level.getBlockEntity(pos) instanceof MixerEntity be?be:null;}
 @Override public boolean isLoaded(){return !isRemoved()&&core()!=null;}@Override public boolean canConnect(Direction side){return side==Direction.UP&&core()!=null;}@Override public boolean canConnect(HbmFluidType type,Direction side){return canConnect(side);}@Override public long getPower(){var c=core();return c==null?0:c.getPower();}@Override public long getMaxPower(){return 10000;}@Override public void setPower(long value){var c=core();if(c!=null)c.setPower(value);}@Override public BigInteger getPowerExact(){var c=core();return c==null?BigInteger.ZERO:c.power;}@Override public BigInteger getMaxPowerExact(){return MixerEntity.CAPACITY;}@Override public void setPowerExact(BigInteger value){var c=core();if(c!=null)c.setPowerExact(value);}
 @Override public FluidTank[] getAllTanks(){var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getAllTanks();}@Override public FluidTank[] getReceivingTanks(){var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getReceivingTanks();}@Override public FluidTank[] getSendingTanks(){var c=core();return c==null?FluidTank.EMPTY_ARRAY:c.getSendingTanks();}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){var c=core();return c==null?amount:c.transferFluid(type,pressure,amount);}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){var c=core();if(c!=null)c.useUpFluid(type,pressure,amount);}@Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return 10000;}@Override public List<TankView> tanks(){var c=core();return c==null?List.of():c.tanks();}@Override public List<ProgressView> progressBars(){var c=core();return c==null?List.of():c.progressBars();}
}
