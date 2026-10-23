package com.hbm.network.rtty.ror;
import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.computer.*;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Original eight value channels, forced initial rebroadcast and seven zero-indexed OC→CC callbacks. */
public final class RadioReaderEntity extends RadioRorEntity {
 public final String[] channels=new String[8],names=new String[8],prev=new String[8];private final boolean[] forceUpdate=new boolean[8];
 public RadioReaderEntity(BlockPos pos,BlockState state){super(RadioRorContent.READER_ENTITY.get(),pos,state);Arrays.fill(channels,"");Arrays.fill(names,"");Arrays.fill(prev,"");Arrays.fill(forceUpdate,true);}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioReaderEntity reader){if(level instanceof ServerLevel server)reader.serverTick(server);}
 @Override protected boolean signalTick(ServerLevel server){if(support() instanceof IRORValueProvider provider)for(int i=0;i<8;i++){String channel=channels[i],name=names[i],previous=prev[i];if(channel==null||channel.isEmpty()||name==null||name.isEmpty())continue;String value=provider.provideRORValue(IRORValueProvider.PREFIX_VALUE+name.toLowerCase(Locale.US));if(value!=null&&(polling||forceUpdate[i]||!value.equals(previous))){RTTYSystem.broadcast(server,channel,value);prev[i]=value;forceUpdate[i]=false;}}return true;}
 private void setChannel(int i,String value){if(!value.equals(channels[i])){channels[i]=value;forceUpdate[i]=true;}}private void setName(int i,String value){if(!value.equals(names[i])){names[i]=value;forceUpdate[i]=true;}}
 @Override public void control(CompoundTag tag){if(tag.contains("p"))polling=tag.getBoolean("p");for(int i=0;i<8;i++){if(tag.contains("c"+i))setChannel(i,tag.getString("c"+i));if(tag.contains("n"+i))setName(i,tag.getString("n"+i));}setChanged();}
 @Override protected void saveState(CompoundTag tag){tag.putBoolean("p",polling);for(int i=0;i<8;i++){tag.putString("c"+i,channels[i]);tag.putString("n"+i,names[i]);tag.putString("p"+i,prev[i]);}}@Override protected void loadState(CompoundTag tag){polling=tag.getBoolean("p");for(int i=0;i<8;i++){channels[i]=tag.getString("c"+i);names[i]=tag.getString("n"+i);prev[i]=tag.getString("p"+i);}}
 @Override public String computerType(){return "radio_reader";}@Override public String[] computerMethods(){return new String[]{"setChannel","getChannel","setName","getName","setPolling","getPolling","read"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){if(method.equals("setPolling")){polling=args.checkBoolean(0);setChanged();return new Object[0];}if(method.equals("getPolling"))return new Object[]{polling};if(!Arrays.asList(computerMethods()).contains(method))throw new ComputerException("No such method: "+method);int i=args.checkInteger(0);if(i<0||i>=8)return new Object[0];switch(method){case "setChannel"->setChannel(i,args.checkString(1));case "setName"->setName(i,args.checkString(1));case "getChannel"->{return new Object[]{channels[i]};}case "getName"->{return new Object[]{names[i]};}case "read"->{return new Object[]{prev[i]};}default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[0];}
}
