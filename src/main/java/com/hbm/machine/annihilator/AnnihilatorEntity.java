package com.hbm.machine.annihilator;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.hazard.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Real no-HE annihilator: source slot/request order, shared BigInteger totals and destructive release effects. */
public final class AnnihilatorEntity extends ThermalMachine implements StandardFluidReceiver {
 public String pool="Recycling";public BigInteger monitorBigInt=BigInteger.ZERO;
 public final FluidTank tank=new FluidTank(HbmFluids.NONE,2500000);
 private final Map<BlockPos,FluidPorts> ports=new LinkedHashMap<>();
 public AnnihilatorEntity(BlockPos pos,BlockState state){super(AnnihilatorContent.ENTITY.get(),pos,state,11);}
 @Override protected String title(){return "container.annihilator";}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new AnnihilatorMenu(id,inventory,this,worldPosition);}
 public static BlockPos[] portPositions(BlockPos core,Direction dir){var rot=dir.getClockWise();return new BlockPos[]{core.relative(dir,4),core.relative(dir,3).relative(rot),core.relative(dir,3).relative(rot,-1)};}
 public static Direction portSide(BlockPos core,Direction dir,BlockPos port){return port.equals(core.relative(dir,4))?dir:port.equals(core.relative(dir,3).relative(dir.getClockWise()))?dir.getClockWise():dir.getCounterClockWise();}
 public static void tick(Level level,BlockPos pos,BlockState state,AnnihilatorEntity self){if(level instanceof ServerLevel server)self.serverTick(server);}
 private void updatePorts(ServerLevel server){var dir=DummyableBlock.facing(getBlockState());var loaded=new HashSet<BlockPos>();for(var pos:portPositions(worldPosition,dir))if(server.hasChunkAt(pos)&&server.getBlockState(pos).is(getBlockState().getBlock())&&DummyableBlock.isPort(server.getBlockState(pos))){loaded.add(pos);ports.computeIfAbsent(pos,key->{var side=portSide(worldPosition,dir,key);return new FluidPorts(new BlockPos[]{key},new NodeConnection[]{new NodeConnection(key.relative(side),side)});}).update(server,this);}for(var old:List.copyOf(ports.keySet()))if(!loaded.contains(old))ports.remove(old).remove(server);}
 private void serverTick(ServerLevel server){updatePorts(server);tank.setType(1,slots.toArray(ItemStack[]::new));if(pool!=null&&!pool.isEmpty()){var data=AnnihilatorSavedData.get(server);boolean didSomething=false;if(!slots.getFirst().isEmpty()){onDestroy(slots.getFirst());tryAddPayout(data.push(pool,slots.getFirst(),false));slots.set(0,ItemStack.EMPTY);setChanged();didSomething=true;}if(tank.getFill()>0){tank.getTankType().release(server,worldPosition,tank,tank.getFill()*2,FluidTraits.Release.BURN);tryAddPayout(data.push(pool,tank.getTankType(),tank.getFill(),false));tank.setFill(0);setChanged();didSomething=true;}if(didSomething){var dir=DummyableBlock.facing(getBlockState());double x=worldPosition.getX()+.5-dir.getStepX()*3,z=worldPosition.getZ()+.5-dir.getStepZ()*3;com.hbm.particle.LegacyFluidEffects.gasFlame(server,x,worldPosition.getY()+8.75,z,server.random.nextGaussian()*.05,.1,server.random.nextGaussian()*.05);if(server.getGameTime()%3==0)server.playSound(null,x,worldPosition.getY()+8.75,z,com.hbm.registry.HbmSounds.get("weapon.flamethrowerShoot"),net.minecraft.sounds.SoundSource.BLOCKS,isMuffled()?.1F:1F,.5F+server.random.nextFloat()*.25F);}if(!slots.get(8).isEmpty()){var monitor=slots.get(8);var key=monitor.getItem() instanceof FluidIdentifier id?AnnihilatorRecipes.Key.fluid(id.getType(server,worldPosition,monitor)):AnnihilatorRecipes.Key.comparable(monitor);monitorBigInt=data.monitor(pool,key);}if(!slots.get(9).isEmpty()){var single=slots.get(9).copyWithCount(1);onDestroy(single);var payout=data.push(pool,single,true);slots.get(9).shrink(1);if(!payout.isEmpty()){if(slots.get(10).isEmpty())slots.set(10,payout);else if(ItemStack.isSameItemSameComponents(slots.get(10),payout)&&slots.get(10).getMaxStackSize()>=slots.get(10).getCount()+payout.getCount())slots.get(10).grow(payout.getCount());}setChanged();}}sync(25);}
 private void onDestroy(ItemStack stack){if(!(level instanceof ServerLevel server))return;double radiation=HazardContext.withLookup(server.registryAccess(),()->HbmHazards.registry().evaluate(stack).firstLevel(HazardType.RADIATION).orElse(0));if(radiation>0){var dir=DummyableBlock.facing(getBlockState());com.hbm.radiation.HbmRadiation.incrementRad(server,worldPosition.getX()-dir.getStepX()*3,worldPosition.getY()+9,worldPosition.getZ()-dir.getStepZ()*3,(float)Math.min(radiation*5,1000));}}
 public void tryAddPayout(ItemStack payout){if(payout.isEmpty())return;for(int i=2;i<=7;i++)if(!slots.get(i).isEmpty()&&ItemStack.isSameItemSameComponents(slots.get(i),payout)&&slots.get(i).getMaxStackSize()>=slots.get(i).getCount()+payout.getCount()){slots.get(i).grow(payout.getCount());setChanged();return;}for(int i=2;i<=7;i++)if(slots.get(i).isEmpty()){slots.set(i,payout);setChanged();return;}}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 public void receiveControl(Player player,CompoundTag tag){if(!stillValid(player))return;if(tag.contains("pool")&&!tag.getString("pool").isEmpty()){pool=tag.getString("pool");setChanged();}}
 @Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}@Override public boolean canConnect(HbmFluidType type,Direction side){return side!=null;}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{tank};}@Override public FluidTank[] getAllTanks(){return getReceivingTanks();}@Override public ConnectionPriority getFluidPriority(){return ConnectionPriority.LOW;}
 @Override public List<TankView> tanks(){return List.of(new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill()));}
 @Override protected void saveMachine(CompoundTag tag){tank.writeToNBT(tag,"t");tag.putString("pool",pool==null?"":pool);}@Override protected void loadMachine(CompoundTag tag){tank.readFromNBT(tag,"t");pool=tag.getString("pool");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putByteArray("monitor",monitorBigInt.toByteArray());return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);monitorBigInt=tag.contains("monitor")?new BigInteger(tag.getByteArray("monitor")):BigInteger.ZERO;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return slot==0||slot==1&&stack.getItem() instanceof FluidIdentifier||slot==8||slot==9;}@Override public int[] getSlotsForFace(Direction side){return new int[]{0,2,3,4,5,6,7};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>=2&&slot<=7;}
 private void disconnect(){if(level instanceof ServerLevel server)ports.values().forEach(port->port.remove(server));ports.clear();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
}
