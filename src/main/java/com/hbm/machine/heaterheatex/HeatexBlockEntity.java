package com.hbm.machine.heaterheatex;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Exact source coolable-trait conversion, private fluid FIFO and original inlet-before-conversion wire snapshot. */
public final class HeatexBlockEntity extends ThermalMachine implements HeatSource,StandardFluidTransceiver,IFluidCopiable {
 public final FluidTank[] tanks={new FluidTank(HbmFluids.COOLANT_HOT,24000),new FluidTank(HbmFluids.COOLANT,24000)};
 public int amountToCool=24000,tickDelay=1,heatEnergy;private FluidPorts ports;private List<BlockPos> currentPorts=List.of();private CompoundTag inletSnapshot;
 public HeatexBlockEntity(BlockPos pos,BlockState state){super(HeatexContent.ENTITY.get(),pos,state,1);}
 @Override protected String title(){return "container.heaterHeatex";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new HeatexMenu(id,inventory,this,worldPosition);}
 public static BlockPos[] portPositions(BlockPos core,BlockState state){var facing=DummyableBlock.facing(state);var rot=facing.getCounterClockWise();return new BlockPos[]{core.relative(facing).relative(rot),core.relative(facing).relative(rot.getOpposite()),core.relative(facing.getOpposite()).relative(rot),core.relative(facing.getOpposite()).relative(rot.getOpposite())};}
 public static Direction portSide(BlockPos core,BlockState state,BlockPos pos){var facing=DummyableBlock.facing(state);var ports=portPositions(core,state);for(int i=0;i<ports.length;i++)if(ports[i].equals(pos))return i<2?facing:facing.getOpposite();return null;}
 public static void tick(Level level,BlockPos pos,BlockState state,HeatexBlockEntity heater){if(level instanceof ServerLevel server)heater.serverTick(server);}
 private void serverTick(ServerLevel server){updatePorts(server);tanks[0].setType(0,slots.toArray(ItemStack[]::new));setupTanks();heatEnergy*=.999;inletSnapshot=new CompoundTag();tanks[0].writeToNBT(inletSnapshot,"0");tryConvert(server);sync(25);}
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition,getBlockState()))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var side=portSide(worldPosition,getBlockState(),pos);connections.add(new NodeConnection(pos.relative(side),side));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);}
 private void setupTanks(){var trait=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);if(trait!=null&&trait.getEfficiency(FluidTraits.CoolingType.HEATEXCHANGER)>0){tanks[1].setTankType(trait.coolsTo());return;}tanks[0].setTankType(HbmFluids.NONE);tanks[1].setTankType(HbmFluids.NONE);}
 private void tryConvert(ServerLevel server){var trait=tanks[0].getTankType().getTrait(FluidTraits.Coolable.class);if(trait==null)return;if(tickDelay<1)tickDelay=1;if(server.getGameTime()%tickDelay!=0)return;int inputOps=tanks[0].getFill()/trait.amountReq(),outputOps=(tanks[1].getMaxFill()-tanks[1].getFill())/trait.amountProduced();int ops=Math.min(inputOps,Math.min(outputOps,amountToCool));tanks[0].setFill(tanks[0].getFill()-trait.amountReq()*ops);tanks[1].setFill(tanks[1].getFill()+trait.amountProduced()*ops);heatEnergy+=trait.heatEnergy()*ops*trait.getEfficiency(FluidTraits.CoolingType.HEATEXCHANGER);setChanged();}
 public void receiveControl(int field,int value){if(field==0)amountToCool=net.minecraft.util.Mth.clamp(value,1,tanks[0].getMaxFill());if(field==1)tickDelay=Math.max(value,1);setChanged();}
 public void identify(HbmFluidType type){tanks[0].setTankType(type);setChanged();}
 @Override public int getHeatStored(){return heatEnergy;}@Override public void useUpHeat(int amount){heatEnergy=Math.max(0,heatEnergy-amount);setChanged();}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public boolean canConnect(HbmFluidType type,Direction side){var facing=DummyableBlock.facing(getBlockState());return side==facing||side==facing.getOpposite();}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}
 @Override public List<TankView> tanks(){return List.of(new TankView(tanks[0].getTankType(),tanks[0].getFill(),tanks[0].getMaxFill()),new TankView(tanks[1].getTankType(),tanks[1].getFill(),tanks[1].getMaxFill()));}
 @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putInt("toCool",amountToCool);int[] ids=getFluidIDToCopy();if(ids.length>0)tag.putIntArray("fluidID",ids);return tag;}
 @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){int[] ids=tag.getIntArray("fluidID");if(index>=0&&index<ids.length)tanks[0].setTankType(HbmFluids.fromID(ids[index]));if(tag.contains("toCool"))amountToCool=tag.getInt("toCool");setChanged();}
 public String[] getFunctionInfo(){return new String[]{"VAL:hotfluid","VAL:coldfluid","VAL:heat"};}public String provideRORValue(String name){return switch(name){case "VAL:hotfluid"->Integer.toString(tanks[0].getFill());case "VAL:coldfluid"->Integer.toString(tanks[1].getFill());case "VAL:heat"->Integer.toString(heatEnergy);default->null;};}
 @Override protected void saveMachine(CompoundTag tag){tanks[0].writeToNBT(tag,"0");tanks[1].writeToNBT(tag,"1");tag.putInt("heatEnergy",heatEnergy);tag.putInt("toCool",amountToCool);tag.putInt("delay",tickDelay);}
 @Override protected void loadMachine(CompoundTag tag){tanks[0].readFromNBT(tag,"0");tanks[1].readFromNBT(tag,"1");heatEnergy=tag.getInt("heatEnergy");amountToCool=tag.getInt("toCool");tickDelay=tag.getInt("delay");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();if(inletSnapshot!=null)tag.merge(inletSnapshot);else tanks[0].writeToNBT(tag,"0");tanks[1].writeToNBT(tag,"1");tag.putInt("heatEnergy",heatEnergy);tag.putInt("toCool",amountToCool);tag.putInt("delay",tickDelay);return tag;}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;currentPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
}
