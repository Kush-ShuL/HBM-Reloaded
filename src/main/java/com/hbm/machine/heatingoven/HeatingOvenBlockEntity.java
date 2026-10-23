package com.hbm.machine.heatingoven;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.heat.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.pollution.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Source HeaterOven: lossy heat pull precedes the exact finite two-slot FireboxBase burn/cool and smoke cycle. */
public final class HeatingOvenBlockEntity extends ThermalMachine implements HeatSource,StandardFluidSender, com.hbm.multiblock.MuffledMachine {
 public int maxBurnTime,burnTime,burnHeat,heatEnergy,playersUsing;public boolean wasOn,muffled;public float doorAngle,previousDoorAngle;
 public final FluidTank smoke=new FluidTank(HbmFluids.SMOKE,50),leaded=new FluidTank(HbmFluids.SMOKE_LEADED,50),poison=new FluidTank(HbmFluids.SMOKE_POISON,50);private FluidPorts ports;private List<BlockPos> currentPorts=List.of();
 public HeatingOvenBlockEntity(BlockPos pos,BlockState state){super(HeatingOvenContent.ENTITY.get(),pos,state,2);}
 @Override protected String title(){return "container.heaterOven";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new HeatingOvenMenu(id,inventory,this,worldPosition);}
 public static void tick(Level level,BlockPos pos,BlockState state,HeatingOvenBlockEntity self){if(level instanceof ServerLevel server)self.serverTick(server);else{self.previousDoorAngle=self.doorAngle;float speed=self.doorAngle/10F+3;self.doorAngle=net.minecraft.util.Mth.clamp(self.doorAngle+(self.playersUsing>0?speed:-speed),0,135);if(self.wasOn&&level.getGameTime()%5==0){var dir=DummyableBlock.facing(state);level.addParticle(ParticleTypes.FLAME,pos.getX()+.5+dir.getStepX()+level.random.nextDouble()*.5-.25,pos.getY()+.25+level.random.nextDouble()*.25,pos.getZ()+.5+dir.getStepZ()+level.random.nextDouble()*.5-.25,0,0,0);}}}
 private void pullHeat(){if(level.getBlockEntity(worldPosition.below()) instanceof HeatSource source){int toPull=Math.max(Math.min(source.getHeatStored(),HeatingOvenConfig.maxHeat-heatEnergy),0);heatEnergy+=toPull*HeatingOvenConfig.heatEfficiency;source.useUpHeat(toPull);}}
 private void serverTick(ServerLevel server){
  pullHeat();
  var loaded=new ArrayList<BlockPos>();for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++){if(x==0&&z==0)continue;BlockPos pos=worldPosition.offset(x,0,z);if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock()))loaded.add(pos);}
  if(!loaded.equals(currentPorts)){disconnect();currentPorts=List.copyOf(loaded);if(!loaded.isEmpty()){var connections=new ArrayList<NodeConnection>();for(var pos:loaded){int x=pos.getX()-worldPosition.getX(),z=pos.getZ()-worldPosition.getZ();if(x==-1)connections.add(new NodeConnection(pos.west(),Direction.WEST));if(x==1)connections.add(new NodeConnection(pos.east(),Direction.EAST));if(z==-1)connections.add(new NodeConnection(pos.north(),Direction.NORTH));if(z==1)connections.add(new NodeConnection(pos.south(),Direction.SOUTH));}ports=new FluidPorts(loaded.toArray(BlockPos[]::new),connections.toArray(NodeConnection[]::new));}}if(ports!=null)ports.update(server,this);
  wasOn=false;if(burnTime<=0){for(int i=0;i<2;i++){var stack=slots.get(i);if(stack.isEmpty())continue;int sourceTime=HeatingOvenConfig.BURN.getBurnTime(stack);if(sourceTime>0){int fuel=(int)(sourceTime*HeatingOvenConfig.timeMultiplier);if(server.getBlockEntity(worldPosition.below()) instanceof AshReceiver ash)ash.addFuelAsh(stack,sourceTime);maxBurnTime=burnTime=fuel;burnHeat=HeatingOvenConfig.BURN.getBurnHeat(HeatingOvenConfig.baseHeat,stack);var container=stack.getCraftingRemainingItem();stack.shrink(1);if(stack.isEmpty())slots.set(i,container);wasOn=true;break;}}}
  else{if(heatEnergy<HeatingOvenConfig.maxHeat){burnTime--;if(server.getGameTime()%20==0)pollute(server,PollutionType.SOOT,PollutionSystem.SOOT_PER_SECOND*3);}wasOn=true;if(server.random.nextInt(15)==0&&!muffled)server.playSound(null,worldPosition,SoundEvents.FIRE_AMBIENT,SoundSource.BLOCKS,1,.5F+server.random.nextFloat()*.5F);}
  if(wasOn)heatEnergy=Math.min(heatEnergy+burnHeat,HeatingOvenConfig.maxHeat);else{heatEnergy=Math.max(heatEnergy-Math.max(heatEnergy/1000,1),0);burnHeat=0;}sync(50);
 }
 private void pollute(ServerLevel level,PollutionType type,float amount){FluidTank tank=type==PollutionType.SOOT?smoke:type==PollutionType.HEAVYMETAL?leaded:poison;int fluidAmount=(int)Math.ceil(amount*100);tank.setFill(tank.getFill()+fluidAmount);if(tank.getFill()>tank.getMaxFill()){int overflow=tank.getFill()-tank.getMaxFill();tank.setFill(tank.getMaxFill());PollutionSystem.increment(level,worldPosition,type,overflow/100F);if(level.random.nextInt(3)==0)level.playSound(null,worldPosition,SoundEvents.LAVA_EXTINGUISH,SoundSource.BLOCKS,.1F,1.5F);}}
 private void disconnect(){if(level instanceof ServerLevel server&&ports!=null)ports.remove(server);ports=null;currentPorts=List.of();}@Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
 @Override public int getHeatStored(){return heatEnergy;}@Override public void useUpHeat(int heat){heatEnergy=Math.max(0,heatEnergy-heat);setChanged();}
 @Override public boolean canConnect(HbmFluidType type,Direction side){return side!=Direction.UP&&side!=Direction.DOWN&&HbmFluids.isSmoke(type);}@Override public FluidTank[] getAllTanks(){return FluidTank.EMPTY_ARRAY;}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{smoke,leaded,poison};}@Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidSender.super.useUpFluid(type,pressure,amount);setChanged();}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return HeatingOvenConfig.BURN.getBurnTime(stack)>0;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,1};}
 @Override public void startOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing++;}@Override public void stopOpen(Player player){if(level!=null&&!level.isClientSide)playersUsing--;}
 @Override protected void saveMachine(CompoundTag tag){tag.putInt("maxBurnTime",maxBurnTime);tag.putInt("burnTime",burnTime);tag.putInt("burnHeat",burnHeat);tag.putInt("heatEnergy",heatEnergy);tag.putInt("playersUsing",playersUsing);tag.putBoolean("wasOn",wasOn);tag.putBoolean("muffled",muffled);smoke.writeToNBT(tag,"smoke0");leaded.writeToNBT(tag,"smoke1");poison.writeToNBT(tag,"smoke2");}
 @Override protected void loadMachine(CompoundTag tag){maxBurnTime=tag.getInt("maxBurnTime");burnTime=tag.getInt("burnTime");burnHeat=tag.getInt("burnHeat");heatEnergy=tag.getInt("heatEnergy");if(level!=null&&level.isClientSide){playersUsing=tag.getInt("playersUsing");wasOn=tag.getBoolean("wasOn");}muffled=tag.getBoolean("muffled");smoke.readFromNBT(tag,"smoke0");leaded.readFromNBT(tag,"smoke1");poison.readFromNBT(tag,"smoke2");}
 @Override public List<ProgressView> progressBars(){var bars=new ArrayList<ProgressView>();bars.add(new ProgressView("jade.hbm.crucible.heat",heatEnergy,HeatingOvenConfig.maxHeat,0xE69B51));if(burnTime>0)bars.add(new ProgressView("jade.hbm.fuel_remaining",burnTime,maxBurnTime,0xE69B51));return bars;}@Override public List<TankView> tanks(){return List.of(new TankView(smoke.getTankType(),smoke.getFill(),smoke.getMaxFill()),new TankView(leaded.getTankType(),leaded.getFill(),leaded.getMaxFill()),new TankView(poison.getTankType(),poison.getFill(),poison.getMaxFill()));}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
