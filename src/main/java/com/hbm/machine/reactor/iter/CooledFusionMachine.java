package com.hbm.machine.reactor.iter;

import com.hbm.energy.EnergyReceiver;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidTransceiver;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Actual TileEntityCooledBase fields, .5K/mB cryogenic conversion and source temperature predicate. No substitute coolant/heat source. */
public abstract class CooledFusionMachine extends ThermalMachine implements EnergyReceiver,StandardFluidTransceiver, com.hbm.multiblock.MuffledMachine {
    public static final float KELVIN=273F,TEMPERATURE_TARGET=KELVIN-150F,TEMP_CHANGE_PER_MB=.5F,TEMP_PASSIVE_HEATING=2.5F,TEMP_CHANGE_MAX=5F+TEMP_PASSIVE_HEATING;
    public final FluidTank[] coolantTanks={new FluidTank(HbmFluids.fromName("PERFLUOROMETHYL_COLD"),4000),new FluidTank(HbmFluids.fromName("PERFLUOROMETHYL"),4000)};
    protected BigInteger power=BigInteger.ZERO;
    public float temperature=KELVIN+20;
    public boolean muffled,tilted;
    protected CooledFusionMachine(BlockEntityType<?> type,BlockPos pos,BlockState state,int slots){super(type,pos,state,slots);}
    protected final void updateCryogenics(){temperature+=TEMP_PASSIVE_HEATING;if(temperature>KELVIN+20)temperature=KELVIN+20;if(temperature>TEMPERATURE_TARGET){int thermal=(int)Math.ceil(Math.min(temperature-TEMPERATURE_TARGET,TEMP_CHANGE_MAX)/TEMP_CHANGE_PER_MB);int cold=coolantTanks[0].getFill(),hot=coolantTanks[1].getMaxFill()-coolantTanks[1].getFill();int cycles=Math.min(thermal,Math.min(cold,hot));coolantTanks[0].setFill(cold-cycles);coolantTanks[1].setFill(coolantTanks[1].getFill()+cycles);temperature-=TEMP_CHANGE_PER_MB*cycles;}}
    public final boolean isCool(){return temperature<=TEMPERATURE_TARGET;}
    @Override public long getPower(){return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}@Override public BigInteger getPowerExact(){return power;}@Override public void setPower(long amount){power=BigInteger.valueOf(amount);}@Override public void setPowerExact(BigInteger amount){power=amount;}
    @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
    @Override public FluidTank[] getAllTanks(){return coolantTanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{coolantTanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{coolantTanks[1]};}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}
    protected final void saveCooling(CompoundTag tag){for(int i=0;i<2;i++)coolantTanks[i].writeToNBT(tag,"t"+i);tag.putFloat("temperature",temperature);tag.putString("power",power.toString());tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    protected final void loadCooling(CompoundTag tag){for(int i=0;i<2;i++)coolantTanks[i].readFromNBT(tag,"t"+i);temperature=tag.getFloat("temperature");power=tag.contains("power",8)?new BigInteger(tag.getString("power")):BigInteger.valueOf(tag.getLong("power"));muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
