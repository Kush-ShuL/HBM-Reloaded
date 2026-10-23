package com.hbm.network.fluidcontrols;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.ConnectionPriority;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Only the physical outer face of each source proxy is a native fluid port. No HE or vanilla capability. */
public final class PumpPort extends BlockEntity implements FluidProvider,FluidReceiver,MachineView,com.hbm.multiblock.proxy.SourceRebindableProxy,com.hbm.api.computer.ComputerControl {
 public PumpPort(BlockPos pos,BlockState state){super(FluidControlsContent.PORT.get(),pos,state);}
 public PumpEntity core(){return level!=null&&getBlockState().getBlock() instanceof FlowPumpBlock block?block.core(level,worldPosition):null;}
 private com.hbm.multiblock.proxy.SourceProxyBindings.Bound binding(){return com.hbm.multiblock.proxy.SourceProxyBindings.get(this);}
 private boolean out(PumpEntity core){return worldPosition.equals(core.outputPos());}
 @Override public boolean canConnect(HbmFluidType type,Direction side){var b=binding();if(b!=null)return b.canConnect(type,side);var core=core();return core!=null&&side==(out(core)?core.direction():core.direction().getOpposite());}
 @Override public FluidTank[] getAllTanks(){var b=binding();if(b!=null)return b.getAllTanks();var core=core();return core==null?FluidTank.EMPTY_ARRAY:core.getAllTanks();}
 @Override public long getFluidAvailable(HbmFluidType type,int pressure){var b=binding();if(b!=null)return 0;var core=core();return core!=null&&out(core)?core.getFluidAvailable(type,pressure):0;}
 @Override public long getProviderSpeed(HbmFluidType type,int pressure){var b=binding();if(b!=null)return 0;var core=core();return core!=null&&out(core)?core.getProviderSpeed(type,pressure):0;}
 @Override public int[] getProvidingPressureRange(HbmFluidType type){var b=binding();if(b!=null)return DEFAULT_PRESSURE_RANGE;var core=core();return core!=null&&out(core)?core.getProvidingPressureRange(type):DEFAULT_PRESSURE_RANGE;}
 @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){if(binding()!=null)return;var core=core();if(core!=null&&out(core))core.useUpFluid(type,pressure,amount);}
 @Override public long getDemand(HbmFluidType type,int pressure){var b=binding();if(b!=null)return b.getDemand(type,pressure);var core=core();return core!=null&&!out(core)?core.getDemand(type,pressure):0;}
 @Override public long getReceiverSpeed(HbmFluidType type,int pressure){var b=binding();if(b!=null)return b.getReceiverSpeed(type,pressure);var core=core();return core!=null&&!out(core)?core.getReceiverSpeed(type,pressure):0;}
 @Override public int[] getReceivingPressureRange(HbmFluidType type){var b=binding();if(b!=null)return b.getReceivingPressureRange(type);var core=core();return core!=null&&!out(core)?core.getReceivingPressureRange(type):DEFAULT_PRESSURE_RANGE;}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){var b=binding();if(b!=null)return b.transferFluid(type,pressure,amount);var core=core();return core!=null&&!out(core)?core.transferFluid(type,pressure,amount):amount;}
 @Override public ConnectionPriority getFluidPriority(){var core=core();return core==null?ConnectionPriority.NORMAL:core.priority;}
 @Override public long energyStored(){return 0;}@Override public long energyCapacity(){return 0;}@Override public List<TankView> tanks(){var b=binding();if(b!=null)return b.tanks();var core=core();return core==null?List.of():core.tanks();}
 @Override public BlockEntity sourceProxyFallbackTarget(){return core();}
 @Override public String computerType(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerType();}
 @Override public String[] computerMethods(){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).computerMethods();}
 @Override public Object[] callComputer(String method,com.hbm.api.computer.ComputerArguments args){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).callComputer(method,args);}
 @Override public boolean canComputerConnect(Direction side){return com.hbm.multiblock.proxy.SourceProxyBindings.delegate(this).canComputerConnect(side);}
 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}
 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

}
