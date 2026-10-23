package com.hbm.machine.coker;
import com.hbm.api.computer.*;
import com.hbm.api.machine.*;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.heat.HeatSource;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.pollution.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Actual Coker thermal diffusion,2slots/private16k+8kfluids,one20000TUoperation and sourcepollution. No HE capability. */
public final class CokerEntity extends ThermalMachine implements StandardFluidTransceiver,IFluidCopiable,ComputerControl {
 public static final int PROCESS_TIME=20000,MAX_HEAT=100000;public static Consumer<CokerEntity> clientTick=ignored->{};public int heat,progress;public boolean wasOn,hasTask;
 public final FluidTank[] tanks={new FluidTank(HbmFluids.fromName("HEAVYOIL"),16000),new FluidTank(HbmFluids.fromName("OIL_COKER"),8000)};private FluidPorts ports;private List<BlockPos> boundPorts=List.of();
 public CokerEntity(BlockPos pos,BlockState state){super(CokerContent.ENTITY.get(),pos,state,2);}@Override public boolean isValidBlockState(BlockState state){return getType().isValid(state)&&DummyableBlock.isCore(state);}@Override protected String title(){return "container.machineCoker";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CokerMenu(id,inventory,this,worldPosition);}
 public static BlockPos[] portPositions(BlockPos p){return new BlockPos[]{p.offset(-1,0,-1),p.offset(1,0,-1),p.offset(1,0,1),p.offset(-1,0,1)};}public static Direction[] portSides(BlockPos core,BlockPos p){return new Direction[]{p.getX()<core.getX()?Direction.WEST:Direction.EAST,p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH};}
 public static void tick(Level level,BlockPos pos,BlockState state,CokerEntity machine){if(level instanceof ServerLevel server)machine.tickServer(server);else clientTick.accept(machine);}
 private void updatePorts(ServerLevel server){var available=new ArrayList<BlockPos>();for(var p:portPositions(worldPosition))if(server.hasChunkAt(p)&&server.getBlockState(p).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(p)))available.add(p);if(!available.equals(boundPorts)){disconnect();boundPorts=List.copyOf(available);if(!available.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var p:available)for(var face:portSides(worldPosition,p))connections.add(new NodeConnection(p.relative(face),face));ports=new FluidPorts(available.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);}
 private void tickServer(ServerLevel server){updatePorts(server);pullHeat();var array=slots.toArray(ItemStack[]::new);if(tanks[0].setType(0,array))setChanged();wasOn=false;hasTask=canProcess();if(hasTask){int burn=heat/100;if(burn>0){wasOn=true;progress+=burn;heat-=burn;if(progress>=PROCESS_TIME){progress-=PROCESS_TIME;var recipe=CokerRecipeConfig.find(server.getRecipeManager(),tanks[0].getTankType());var output=recipe.solid();if(!output.isEmpty()){if(slots.get(1).isEmpty())slots.set(1,output.copy());else slots.get(1).grow(output.getCount());}var byproduct=recipe.byproduct();if(byproduct!=null)tanks[1].setFill(tanks[1].getFill()+byproduct.fill);tanks[0].setFill(tanks[0].getFill()-recipe.input().fill);setChanged();}}if(wasOn&&server.getGameTime()%5==0)PollutionSystem.increment(server,worldPosition,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*5);}sync(25);}
 public boolean canProcess(){if(level==null)return false;var recipe=CokerRecipeConfig.find(level.getRecipeManager(),tanks[0].getTankType());if(recipe==null)return false;var byproduct=recipe.byproduct();if(byproduct!=null)tanks[1].setTankType(byproduct.type);if(tanks[0].getFill()<recipe.input().fill)return false;if(byproduct!=null&&byproduct.fill+tanks[1].getFill()>tanks[1].getMaxFill())return false;var output=recipe.solid();var existing=slots.get(1);if(!output.isEmpty()&&!existing.isEmpty()){if(!output.is(existing.getItem()))return false;if(output.getOrDefault(HbmHazards.LEGACY_METADATA.get(),output.getDamageValue()).intValue()!=existing.getOrDefault(HbmHazards.LEGACY_METADATA.get(),existing.getDamageValue()).intValue())return false;if(output.getCount()+existing.getCount()>output.getMaxStackSize())return false;}return true;}
 private void pullHeat(){if(heat>=MAX_HEAT)return;var below=level.getBlockEntity(worldPosition.below());if(below instanceof HeatSource source){int diff=source.getHeatStored()-heat;if(diff==0)return;if(diff>0){diff=(int)Math.ceil(diff*.25);source.useUpHeat(diff);heat+=diff;if(heat>MAX_HEAT)heat=MAX_HEAT;return;}}heat=Math.max(heat-Math.max(heat/1000,1),0);}
 @Override protected void saveMachine(CompoundTag tag){tanks[0].writeToNBT(tag,"t0");tanks[1].writeToNBT(tag,"t1");tag.putInt("prog",progress);tag.putInt("heat",heat);}@Override protected void loadMachine(CompoundTag tag){tanks[0].readFromNBT(tag,"t0");tanks[1].readFromNBT(tag,"t1");progress=tag.getInt("prog");heat=tag.getInt("heat");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putBoolean("wasOn",wasOn);tag.putBoolean("hbm_task",hasTask);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);wasOn=tag.getBoolean("wasOn");hasTask=tag.getBoolean("hbm_task");}
 @Override public FluidTank[] getAllTanks(){return tanks;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tanks[0]};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{tanks[1]};}@Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[]{1};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
 @Override public List<TankView> tanks(){return Arrays.stream(tanks).map(t->new TankView(t.getTankType(),t.getFill(),t.getMaxFill())).toList();}@Override public List<ProgressView> progressBars(){return hasTask?List.of(new ProgressView("jade.hbm.processing",progress,PROCESS_TIME,0xC7C1A3)):List.of();}
 private void disconnect(){if(ports!=null&&level instanceof ServerLevel server)ports.remove(server);ports=null;boundPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public String computerType(){return "ntm_coker";}@Override public String[] computerMethods(){return new String[]{"getFluidStored","getTypeStored","getHeat","getInfo"};}@Override public Object[] callComputer(String name,ComputerArguments args){return switch(name){case "getFluidStored"->new Object[]{tanks[0].getFill(),tanks[1].getFill()};case "getTypeStored"->new Object[]{tanks[0].getTankType().getName(),tanks[1].getTankType().getName()};case "getHeat"->new Object[]{heat};case "getInfo"->new Object[]{tanks[0].getTankType().getName(),tanks[1].getTankType().getName(),tanks[0].getFill(),tanks[1].getFill(),heat};default->throw new ComputerException("No such method: "+name);};}
}
