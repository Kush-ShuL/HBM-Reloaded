package com.hbm.machine.oilburner;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.api.machine.IFluidCopiable;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.HeatSource;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.pollution.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Source Oilburner, including source finite fuel loading, per-mB truncation and over-cap/empty-fuel thermal behavior. */
public final class OilburnerBlockEntity extends ThermalMachine implements HeatSource,StandardFluidTransceiver,IFluidCopiable, com.hbm.multiblock.MuffledMachine {
 public static final int MAX_HEAT=500000;public boolean isOn,muffled,tilted;public int burnRate=1,maxBurnRate=10,maxHeatEnergy=MAX_HEAT,heatEnergy;
 public final FluidTank tank=new FluidTank(HbmFluids.HEATINGOIL,16000),smoke=new FluidTank(HbmFluids.SMOKE,100),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,100),poison=new FluidTank(HbmFluids.SMOKE_POISON,100);private FluidPorts ports;private List<BlockPos> currentPorts=List.of();
 public OilburnerBlockEntity(BlockPos pos,BlockState state){super(OilburnerContent.ENTITY.get(),pos,state,4);}
 @Override protected String title(){return "container.heaterOilburner";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new OilburnerMenu(id,inventory,this,worldPosition);}
 public static BlockPos[] portPositions(BlockPos core){return new BlockPos[]{core.north(),core.east(),core.south(),core.west()};}
 public static Direction portSide(BlockPos core,BlockPos pos){for(var side:Direction.Plane.HORIZONTAL)if(core.relative(side).equals(pos))return side;return null;}
 public static BlockPos heatPort(BlockPos core,BlockState state){return core.relative(DummyableBlock.facing(state).getOpposite()).above();}
 public int heatPerMb(){var trait=tank.getTankType().getTrait(FluidTraits.Flammable.class);return trait==null?0:(int)(trait.getHeatEnergy()/1000);}
 public int lookHeatPerTick(){var trait=tank.getTankType().getTrait(FluidTraits.Flammable.class);return trait==null?0:(int)(trait.getHeatEnergy()*burnRate/1000);}
 public static void tick(Level level,BlockPos pos,BlockState state,OilburnerBlockEntity heater){if(level instanceof ServerLevel server)heater.serverTick(server);}
 private void serverTick(ServerLevel server){
  updatePorts(server);
  var items=slots.toArray(ItemStack[]::new);tank.loadTank(0,1,items);tank.setType(2,items);
  for(int i=0;i<items.length;i++)slots.set(i,items[i]==null?ItemStack.EMPTY:items[i]);
  int overdrive=slots.get(3).getItem() instanceof OverdriveUpgradeItem upgrade?Math.min(3,upgrade.tier()):0;
  maxBurnRate=10*(overdrive+1);maxHeatEnergy=MAX_HEAT*(overdrive+1);
  burnRate=Math.clamp(burnRate,1,maxBurnRate);heatEnergy=Math.min(heatEnergy,maxHeatEnergy);
  boolean shouldCool=true;
  if(isOn&&heatEnergy<maxHeatEnergy){var trait=tank.getTankType().getTrait(FluidTraits.Flammable.class);if(trait!=null){int toBurn=Math.min(burnRate,tank.getFill());tank.setFill(tank.getFill()-toBurn);int heat=(int)(trait.getHeatEnergy()/1000);heatEnergy+=heat*toBurn;if(server.getGameTime()%5==0&&toBurn>0)polluteFuel(server,toBurn*5);shouldCool=false;}}
  if(heatEnergy>=maxHeatEnergy)shouldCool=false;
  if(shouldCool)heatEnergy=Math.max(heatEnergy-Math.max(heatEnergy/1000,1),0);
  sync(25);
 }
 private void updatePorts(ServerLevel server){var loaded=new ArrayList<BlockPos>();for(var port:portPositions(worldPosition))if(server.hasChunkAt(port)&&server.getBlockState(port).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(port)))loaded.add(port);if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){var side=portSide(worldPosition,pos);connections.add(new NodeConnection(pos.relative(side),side));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);}
 private void polluteFuel(ServerLevel level,int amount){var trait=tank.getTankType().getTrait(FluidTraits.Polluting.class);if(trait!=null)trait.burnMap().forEach((type,rate)->pollute(level,type,rate*amount));}
 private void pollute(ServerLevel level,PollutionType type,float amount){var output=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?leaded:poison;output.setFill(output.getFill()+(int)Math.ceil(amount*100));if(output.getFill()>output.getMaxFill()){int overflow=output.getFill()-output.getMaxFill();output.setFill(output.getMaxFill());PollutionSystem.increment(level,worldPosition,type,overflow/100F);if(level.random.nextInt(3)==0)level.playSound(null,worldPosition,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}}
 public void toggle(){isOn=!isOn;setChanged();}public void setBurnRate(int value){burnRate=Math.clamp(value,1,maxBurnRate);setChanged();}
 @Override public int getHeatStored(){return heatEnergy;}@Override public void useUpHeat(int amount){heatEnergy=Math.max(0,heatEnergy-amount);setChanged();}
 @Override public FluidTank[] getAllTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{smoke,leaded,poison};}
 @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long left=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(left!=amount)setChanged();return left;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}
 @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()),new TankView(smoke.getTankType(),smoke.getFill(),smoke.getMaxFill()),new TankView(leaded.getTankType(),leaded.getFill(),leaded.getMaxFill()),new TankView(poison.getTankType(),poison.getFill(),poison.getMaxFill()));}@Override public List<ProgressView> progressBars(){return List.of(new ProgressView("jade.hbm.crucible.heat",heatEnergy,maxHeatEnergy,0xE69B51));}
 @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putIntArray("fluidID",new int[]{tank.getTankType().getID()});tag.putInt("burnRate",burnRate);tag.putBoolean("isOn",isOn);return tag;}
 @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){var ids=tag.getIntArray("fluidID");if(index>=0&&index<ids.length)tank.setTankType(HbmFluids.fromID(ids[index]));if(tag.contains("isOn"))isOn=tag.getBoolean("isOn");if(tag.contains("burnRate"))setBurnRate(tag.getInt("burnRate"));setChanged();}
 public String[] getFunctionInfo(){return new String[]{"VAL:heat","VAL:fuel","VAL:burnRate","VAL:state","FUN:setstate!active","FUN:setburnrate!rate"};}public String provideRORValue(String name){return switch(name){case "VAL:heat"->Integer.toString(heatEnergy);case "VAL:fuel"->Integer.toString(tank.getFill());case "VAL:burnrate"->Integer.toString(burnRate);case "VAL:state"->isOn?"1":"0";default->null;};}
 public String runRORFunction(String name,String[] parameters){if(parameters.length==0)return null;if(name.equals("FUN:setstate")){isOn=api.hbm.redstoneoverradio.IRORInteractive.parseBool(parameters[0]);setChanged();}if(name.equals("FUN:setburnrate"))setBurnRate(api.hbm.redstoneoverradio.IRORInteractive.parseInt(parameters[0],1,maxBurnRate));return null;}
 @Override protected void saveMachine(CompoundTag tag){tank.writeToNBT(tag,"tank");smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");tag.putBoolean("isOn",isOn);tag.putInt("heatEnergy",heatEnergy);tag.putInt("maxHeatEnergy",maxHeatEnergy);tag.putInt("burnRate",burnRate);tag.putInt("maxBurnRate",maxBurnRate);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 @Override protected void loadMachine(CompoundTag tag){tank.readFromNBT(tag,"tank");smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");isOn=tag.getBoolean("isOn");heatEnergy=tag.getInt("heatEnergy");maxHeatEnergy=tag.getInt("maxHeatEnergy");burnRate=tag.getInt("burnRate");maxBurnRate=tag.getInt("maxBurnRate");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;currentPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
