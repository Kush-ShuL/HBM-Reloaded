package com.hbm.machine.reactor.dfc.components;
import com.hbm.api.computer.*;
import com.hbm.energy.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import java.math.BigInteger;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
/** Ordinary source MachineBase adapter and canonical private HE/fluid port lifecycle. */
public abstract class DfcComponentEntity extends ThermalMachine implements ComputerControl {
 public final DfcComponentKind kind;public BigInteger power=BigInteger.ZERO;public int watts,beam;private PowerNode powerNode;private FluidPorts fluidPorts;
 protected DfcComponentEntity(DfcComponentKind kind,BlockPos pos,BlockState state){super(DfcComponentsContent.entity(kind),pos,state,kind.slots);this.kind=kind;}
 @Override public boolean isValidBlockState(BlockState state){return getType().isValid(state);}
 @Override protected String title(){return kind.title;}
 public Direction direction(){int meta=getBlockState().getValue(DfcComponentBlock.META);return meta<6?Direction.from3DDataValue(meta):null;}
 public abstract void tickServer(ServerLevel level);
 protected final void ports(ServerLevel level){if(this instanceof EnergyReceiver||this instanceof EnergyProvider){if(powerNode==null||powerNode.isExpired()){powerNode=new PowerNode(worldPosition);HbmEnergy.nodespace(level).createNode(powerNode);}if(powerNode.hasValidNet()){if(this instanceof EnergyReceiver receiver)powerNode.addReceiver(receiver);if(this instanceof EnergyProvider provider)powerNode.addProvider(provider);}}if(this instanceof FluidUser user){if(fluidPorts==null)fluidPorts=new FluidPorts(worldPosition);fluidPorts.update(level,user);}}
 private void disconnect(){if(level instanceof ServerLevel server){if(powerNode!=null&&!powerNode.isExpired())HbmEnergy.nodespace(server).destroyNode(powerNode);if(fluidPorts!=null)fluidPorts.remove(server);}powerNode=null;fluidPorts=null;}
 @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
 public boolean canConnect(Direction side){return side!=null;}public boolean isLoaded(){return hasLevel()&&!isRemoved()&&level.hasChunkAt(worldPosition);}
 public long getPower(){return legacy(power);}public BigInteger getPowerExact(){return power;}public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}public void setPowerExact(BigInteger value){power=value;setChanged();}
 public long getMaxPower(){return legacy(getMaxPowerExact());}public BigInteger getMaxPowerExact(){return BigInteger.valueOf(kind.capacity);}public BigInteger getReceiverSpeedExact(){return getMaxPowerExact();}
 public BigInteger transferPowerExact(BigInteger offered){if(offered.signum()<0)throw new IllegalArgumentException("Negative HE");var accepted=offered.min(getMaxPowerExact().subtract(power).max(BigInteger.ZERO));setPowerExact(power.add(accepted));return offered.subtract(accepted);}public long transferPower(long offered){return transferPowerExact(BigInteger.valueOf(offered)).longValueExact();}
 @Override public java.util.List<com.hbm.api.machine.MachineView.TankView> tanks(){return this instanceof FluidUser user?java.util.Arrays.stream(user.getAllTanks()).map(tank->new com.hbm.api.machine.MachineView.TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList():java.util.List.of();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
 protected static long legacy(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
 protected static void writeExact(CompoundTag tag,String key,BigInteger value){tag.putString(key,value.toString());}protected static BigInteger readExact(CompoundTag tag,String key){return tag.contains(key,8)?new BigInteger(tag.getString(key)):BigInteger.valueOf(tag.getLong(key));}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("hbm_beam",beam);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);beam=tag.getInt("hbm_beam");}
 @Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItem(int index,ItemStack item){return false;}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new DfcComponentMenu(kind,id,inventory,this,worldPosition);}
 @Override public String computerType(){return kind.id;}
 public void control(int value,int button){}
}
