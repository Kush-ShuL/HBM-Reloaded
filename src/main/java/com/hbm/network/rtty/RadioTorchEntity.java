package com.hbm.network.rtty;
import com.hbm.api.computer.*;
import com.hbm.multiblock.MuffledMachine;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original RadioTorchBase configuration, persistence, CC counterpart and model-data notification; the existing RTTYSystem stays unique. */
public abstract class RadioTorchEntity extends BlockEntity implements ComputerControl,MuffledMachine {
 public String channel="";public int lastState;public long lastUpdate;public boolean polling,customMap,muffled,tilted;public final String[] mapping=new String[16];protected boolean loadedFromTag;
 protected RadioTorchEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
 protected abstract boolean signalTick(ServerLevel level);
 protected final void tickRadio(ServerLevel server){if(!signalTick(server))return;var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);}
 protected final void stateChanged(boolean neighbors){setChanged();if(level!=null){level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);if(neighbors)level.updateNeighborsAt(worldPosition,getBlockState().getBlock());}}
 public void control(CompoundTag tag){if(tag.contains("p"))polling=tag.getBoolean("p");if(tag.contains("m"))customMap=tag.getBoolean("m");if(tag.contains("c"))channel=tag.getString("c");for(int i=0;i<16;i++)if(tag.contains("m"+i))mapping[i]=tag.getString("m"+i);setChanged();}
 private void save(CompoundTag tag){tag.putBoolean("p",polling);tag.putBoolean("m",customMap);tag.putInt("l",lastState);tag.putLong("u",lastUpdate);if(channel!=null)tag.putString("c",channel);for(int i=0;i<16;i++)if(mapping[i]!=null)tag.putString("m"+i,mapping[i]);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 private void load(CompoundTag tag){polling=tag.getBoolean("p");customMap=tag.getBoolean("m");lastState=tag.getInt("l");lastUpdate=tag.getLong("u");channel=tag.getString("c");for(int i=0;i<16;i++)mapping[i]=tag.getString("m"+i);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);save(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadedFromTag=true;load(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();save(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){int previous=lastState;load(tag);if(level!=null&&level.isClientSide&&previous!=lastState){requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
 @Override public String computerType(){return "radio_torch";}@Override public String[] computerMethods(){return new String[]{"setChannel","getChannel","setPolling","getPolling","setCustomMap","getCustomMap","setCustomMapValues","getCustomMapValues"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){switch(method){case "setChannel"->channel=args.checkString(0);case "getChannel"->{return new Object[]{channel};}case "setPolling"->polling=args.checkBoolean(0);case "getPolling"->{return new Object[]{polling};}case "setCustomMap"->customMap=args.checkBoolean(0);case "getCustomMap"->{return new Object[]{customMap};}case "setCustomMapValues"->{if(!(args.get(0) instanceof Map<?,?> values))throw new ComputerException("bad argument #1 (table expected)");for(int i=1;i<=16;i++)for(var entry:values.entrySet())if(entry.getKey() instanceof Number number&&number.doubleValue()==i&&entry.getValue() instanceof String value){mapping[i-1]=value;break;}}case "getCustomMapValues"->{var values=new LinkedHashMap<Integer,String>();for(int i=0;i<16;i++)if(mapping[i]!=null)values.put(i+1,mapping[i]);return new Object[]{values};}default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{};}
}
