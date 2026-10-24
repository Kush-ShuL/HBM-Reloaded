package com.hbm.machine.electricheater;

import com.hbm.api.machine.*;
import com.hbm.energy.*;
import com.hbm.heat.HeatSource;
import com.hbm.multiblock.DummyableBlock;
import java.math.BigInteger;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source ElectricHeater: native HE,0.999 heat decay,85% pull, then the exact setting-dependent conversion. */
public final class ElectricHeaterBlockEntity extends BlockEntity implements EnergyHandler,SidedEnergyConnector,HeatSource,ICopiable,MachineView, com.hbm.multiblock.MuffledMachine {
 public static Consumer<ElectricHeaterBlockEntity> clientTick=machine->{},clientRemoved=machine->{};
 private BigInteger power=BigInteger.ZERO;public int heatEnergy;public boolean isOn,muffled;private int setting;private PowerNode node;
 public ElectricHeaterBlockEntity(BlockPos pos,BlockState state){super(ElectricHeaterContent.ENTITY.get(),pos,state);}
 @Override public boolean isValidBlockState(BlockState state){return super.isValidBlockState(state)&&DummyableBlock.isCore(state);}
 public int setting(){return setting;}public long consumption(){return (long)(Math.pow(setting,1.4D)*200D);}public int heatGeneration(){return setting*100;}
 public BlockPos port(){return worldPosition.relative(DummyableBlock.facing(getBlockState()),2);}public PowerNode node(){return node;}
 public static void tick(Level level,BlockPos pos,BlockState state,ElectricHeaterBlockEntity machine){if(level instanceof ServerLevel server)machine.serverTick(server);else clientTick.accept(machine);}
 private void serverTick(ServerLevel server){updatePort(server);heatEnergy*=.999;var below=server.getBlockEntity(worldPosition.below());if(below instanceof HeatSource source){heatEnergy+=source.getHeatStored()*.85;source.useUpHeat(source.getHeatStored());}isOn=false;long cost=consumption();if(setting>0&&power.compareTo(BigInteger.valueOf(cost))>=0){power=power.subtract(BigInteger.valueOf(cost));heatEnergy+=heatGeneration();isOn=true;}setChanged();var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<625)player.connection.send(packet);dispatchScope.commit();}}
 private void updatePort(ServerLevel server){BlockPos pos=port();if(!server.hasChunkAt(pos)||!server.getBlockState(pos).is(getBlockState().getBlock())||!DummyableBlock.isPort(server.getBlockState(pos))){disconnect();return;}if(node==null||node.isExpired()){var side=DummyableBlock.facing(getBlockState());node=new PowerNode(pos).setConnections(new NodeConnection(pos.relative(side),side));HbmEnergy.nodespace(server).createNode(node);}if(node.hasValidNet())node.addReceiver(receiver);}
 /** Source receiver's capacity can shrink below its existing balance when a screwdriver lowers the setting.
  * Keep the balance; expose zero demand rather than violate the new network's fixed-capacity invariant. */
 private final EnergyReceiver receiver=new EnergyReceiver(){
  @Override public long getPower(){return ElectricHeaterBlockEntity.this.getPower();}@Override public void setPower(long value){ElectricHeaterBlockEntity.this.setPower(value);}@Override public long getMaxPower(){return boundedLong(getMaxPowerExact());}
  @Override public BigInteger getPowerExact(){return power;}@Override public void setPowerExact(BigInteger value){ElectricHeaterBlockEntity.this.setPowerExact(value);}@Override public BigInteger getMaxPowerExact(){return power.max(ElectricHeaterBlockEntity.this.getMaxPowerExact());}@Override public boolean isLoaded(){return ElectricHeaterBlockEntity.this.isLoaded();}
  @Override public BigInteger transferPowerExact(BigInteger amount){if(amount.signum()<0)throw new IllegalArgumentException("Negative HE transfer");var accepted=amount.min(getMaxPowerExact().subtract(power));setPowerExact(power.add(accepted));return amount.subtract(accepted);}@Override public long transferPower(long amount){return transferPowerExact(BigInteger.valueOf(amount)).longValueExact();}
 };
 public void toggleSetting(){if(++setting>10)setting=0;setChanged();}
 private static long boundedLong(BigInteger value){return value.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();}
 @Override public long getPower(){return boundedLong(power);}@Override public void setPower(long value){setPowerExact(BigInteger.valueOf(value));}@Override public long getMaxPower(){return boundedLong(getMaxPowerExact());}@Override public BigInteger getPowerExact(){return power;}@Override public BigInteger getMaxPowerExact(){return BigInteger.valueOf(consumption()).multiply(BigInteger.valueOf(20));}@Override public void setPowerExact(BigInteger value){power=value;setChanged();}
 @Override public boolean canConnect(Direction side){return false;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null;}
 @Override public int getHeatStored(){return heatEnergy;}@Override public void useUpHeat(int amount){heatEnergy=Math.max(0,heatEnergy-amount);setChanged();}
 @Override public long energyStored(){return getPower();}@Override public long energyCapacity(){return getMaxPower();}
 /** Original EnergyControl info keys, also consumed by this module's Jade overlay. */
 public void provideExtraInfo(CompoundTag data){data.putLong("consumptionHE",consumption());data.putLong("energyTU",getHeatStored());data.putLong("outputTU",heatGeneration());}
 @Override public CompoundTag getSettings(Level level,BlockPos pos){var tag=new CompoundTag();tag.putInt("setting",setting);return tag;}
 @Override public void pasteSettings(CompoundTag tag,int index,Level level,Player player,BlockPos pos){setting=tag.getInt("setting");setChanged();}
 private void disconnect(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
 @Override public void setRemoved(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();if(level!=null&&level.isClientSide)clientRemoved.accept(this);super.onChunkUnloaded();}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putString("powerExact",power.toString());if(power.bitLength()<64)tag.putLong("power",power.longValue());tag.putInt("setting",setting);tag.putInt("heatEnergy",heatEnergy);tag.putBoolean("muffled",muffled);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);power=tag.contains("powerExact")?new BigInteger(tag.getString("powerExact")):BigInteger.valueOf(tag.getLong("power"));setting=tag.getInt("setting");heatEnergy=tag.getInt("heatEnergy");muffled=tag.getBoolean("muffled");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putBoolean("muffled",muffled);tag.putInt("setting",setting);tag.putInt("heatEnergy",heatEnergy);tag.putBoolean("isOn",isOn);return tag;}
 @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){muffled=tag.getBoolean("muffled");setting=tag.getInt("setting");heatEnergy=tag.getInt("heatEnergy");isOn=tag.getBoolean("isOn");}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
