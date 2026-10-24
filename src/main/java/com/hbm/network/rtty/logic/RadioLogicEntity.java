package com.hbm.network.rtty.logic;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.Arrays;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Actual source Logic state machine; deliberately not the sender/receiver's OC/CC component. */
public final class RadioLogicEntity extends BlockEntity implements MuffledMachine {
 public String channel="";public int lastState;public long lastUpdate;public boolean polling,descending,muffled,tilted;public final String[] mapping=new String[16];public final int[] conditions=new int[16];private boolean loadedFromTag;
 public RadioLogicEntity(BlockPos pos,BlockState state){super(RadioLogicContent.ENTITY.get(),pos,state);Arrays.fill(mapping,"");}
 @Override public void onLoad(){super.onLoad();if(!loadedFromTag&&level!=null)lastUpdate=level.getGameTime();}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioLogicEntity logic){if(level instanceof ServerLevel server)logic.tickServer(server);}
 private void tickServer(ServerLevel server){
  if(!channel.isEmpty()||polling){var signal=channel.isEmpty()?null:RTTYSystem.listen(server,channel);if(signal!=null&&(polling||signal.timeStamp>lastUpdate-1&&signal.timeStamp!=-1)){
   String message=""+signal.signal;lastUpdate=server.getGameTime();int next=0;if(signal.timeStamp<lastUpdate-1&&polling)message="0";
   if(descending){for(int i=15;i>=0;i--)if(!mapping[i].isEmpty()&&parseSignal(message,i)){next=i;break;}}else for(int i=0;i<=15;i++)if(!mapping[i].isEmpty()&&parseSignal(message,i)){next=i;break;}
   if(lastState!=next)output(next);
  }else if(polling&&lastState!=0)output(0);}
  var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<2500)player.connection.send(packet);dispatchScope.commit();}
 }
 public boolean parseSignal(String signal,int index){int condition=conditions[index];if(condition<=5){long sig,map;try{sig=Long.parseLong(signal);map=Long.parseLong(mapping[index]);}catch(Exception ignored){return false;}return switch(condition){case 1->sig<=map;case 2->sig>=map;case 3->sig>map;case 4->sig==map;case 5->sig!=map;default->sig<map;};}return switch(condition){case 7->!signal.equals(mapping[index]);case 8->signal.contains(mapping[index]);case 9->!signal.contains(mapping[index]);default->signal.equals(mapping[index]);};}
 private void output(int state){lastState=state;setChanged();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);level.updateNeighborsAt(worldPosition,getBlockState().getBlock());}
 public void control(CompoundTag tag){if(tag.contains("p"))polling=tag.getBoolean("p");if(tag.contains("c"))channel=tag.getString("c");if(tag.contains("d"))descending=tag.getBoolean("d");for(int i=0;i<16;i++){if(tag.contains("m"+i))mapping[i]=tag.getString("m"+i);if(tag.contains("c"+i))conditions[i]=tag.getInt("c"+i);}setChanged();}
 private void save(CompoundTag tag){tag.putBoolean("p",polling);tag.putBoolean("d",descending);tag.putInt("l",lastState);tag.putLong("u",lastUpdate);if(channel!=null)tag.putString("c",channel);for(int i=0;i<16;i++){if(!mapping[i].isEmpty())tag.putString("m"+i,mapping[i]);if(conditions[i]>0)tag.putInt("c"+i,conditions[i]);}tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 private void load(CompoundTag tag){polling=tag.getBoolean("p");descending=tag.getBoolean("d");lastState=tag.getInt("l");lastUpdate=tag.getLong("u");channel=tag.getString("c");for(int i=0;i<16;i++){mapping[i]=tag.getString("m"+i);conditions[i]=tag.getInt("c"+i);}muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);save(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);loadedFromTag=true;load(tag);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();save(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){int previous=lastState;load(tag);if(level!=null&&level.isClientSide&&lastState!=previous){requestModelDataUpdate();level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}}
 @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
