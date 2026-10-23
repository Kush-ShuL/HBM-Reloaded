package com.hbm.multiblock.proxy;
import com.hbm.energy.EnergyReceiver;
import com.hbm.fluid.FluidTank;
import com.hbm.fluid.network.StandardFluidTransceiver;
import java.math.BigInteger;
/** Exact original Chemical/AssemblyFactory coolant delegate: real core HE + existing coolant water/LPS tanks. */
public final class SourceProxyFluidEnergyDelegate implements EnergyReceiver,com.hbm.energy.SidedEnergyConnector,StandardFluidTransceiver {
 private final EnergyReceiver energy;private final StandardFluidTransceiver fluids;
 public SourceProxyFluidEnergyDelegate(EnergyReceiver energy,StandardFluidTransceiver fluids){this.energy=energy;this.fluids=fluids;}
 @Override public long getPower(){return energy.getPower();}@Override public long getMaxPower(){return energy.getMaxPower();}@Override public void setPower(long value){energy.setPower(value);}
 @Override public BigInteger getPowerExact(){return energy.getPowerExact();}@Override public BigInteger getMaxPowerExact(){return energy.getMaxPowerExact();}@Override public void setPowerExact(BigInteger value){energy.setPowerExact(value);}
 @Override public boolean isLoaded(){return energy.isLoaded();}
 @Override public FluidTank[] getAllTanks(){return ((com.hbm.fluid.network.FluidUser)energy).getAllTanks();}@Override public FluidTank[] getReceivingTanks(){return fluids.getReceivingTanks();}@Override public FluidTank[] getSendingTanks(){return fluids.getSendingTanks();}
}
