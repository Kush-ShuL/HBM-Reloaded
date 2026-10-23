package com.hbm.machine.reactor.rbmk.panels.graph;
import com.hbm.api.computer.*;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelEntity;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
/** Two true RTTY histories, source30samples/15seconds, optional bounds and exact saved/wire fields. */
public final class RbmkGraphEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final GraphUnit[] graphs=new GraphUnit[2];
    public RbmkGraphEntity(BlockPos pos,BlockState state){super(RbmkGraphContent.ENTITY.get(),pos,state);for(int i=0;i<2;i++)graphs[i]=new GraphUnit(i);}
    @Override protected void serverTick(ServerLevel level){if(level.getGameTime()%10==0)for(var graph:graphs)graph.update();}
    public final class GraphUnit{
        public boolean active,polling,minBound,maxBound;public String label="",rtty="";public final long[] values=new long[30];public long min,max;
        GraphUnit(int index){label="Graph "+(index+1);}
        void update(){if(!active||rtty==null||rtty.isEmpty())return;var channel=RTTYSystem.listen(level,rtty);if(channel!=null&&channel.timeStamp<level.getGameTime()-1)channel=null;if(channel!=null&&channel.signal!=null){long signal=0;try{signal=Long.parseLong(channel.signal.toString());}catch(Exception ignored){}pushValue(signal);}else if(polling)pushValue(0);}
        public void pushValue(long value){System.arraycopy(values,1,values,0,values.length-1);values[values.length-1]=value;}
        void write(ByteBuf buffer){buffer.writeBoolean(active).writeBoolean(polling);string(buffer,label);string(buffer,rtty);buffer.writeBoolean(minBound);if(minBound)buffer.writeLong(min);buffer.writeBoolean(maxBound);if(maxBound)buffer.writeLong(max);if(active)for(long value:values)buffer.writeLong(value);}
        void read(ByteBuf buffer){active=buffer.readBoolean();polling=buffer.readBoolean();label=string(buffer);rtty=string(buffer);minBound=buffer.readBoolean();if(minBound)min=buffer.readLong();maxBound=buffer.readBoolean();if(maxBound)max=buffer.readLong();if(active)for(int i=0;i<values.length;i++)values[i]=buffer.readLong();}
        void save(CompoundTag tag,int index){tag.putBoolean("active"+index,active);tag.putBoolean("polling"+index,polling);tag.putString("label"+index,label);tag.putString("rtty"+index,rtty);tag.putBoolean("minBound"+index,minBound);tag.putLong("min"+index,min);tag.putBoolean("maxBound"+index,maxBound);tag.putLong("max"+index,max);for(int i=0;i<values.length;i++)tag.putLong("value"+index+"_"+i,values[i]);}
        void load(CompoundTag tag,int index){active=tag.getBoolean("active"+index);polling=tag.getBoolean("polling"+index);label=tag.getString("label"+index);rtty=tag.getString("rtty"+index);minBound=tag.getBoolean("minBound"+index);min=tag.getLong("min"+index);maxBound=tag.getBoolean("maxBound"+index);max=tag.getLong("max"+index);for(int i=0;i<values.length;i++)values[i]=tag.getLong("value"+index+"_"+i);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var graph:graphs)graph.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var graph:graphs)graph.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<2;i++)graphs[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<2;i++)graphs[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag tag){int active=tag.getByte("active"),polling=tag.getByte("polling");for(int i=0;i<2;i++){var graph=graphs[i];graph.active=(active&(1<<i))!=0;graph.polling=(polling&(1<<i))!=0;graph.label=tag.getString("label"+i);graph.rtty=tag.getString("rtty"+i);graph.minBound=tag.contains("min"+i);if(graph.minBound)graph.min=tag.getLong("min"+i);graph.maxBound=tag.contains("max"+i);if(graph.maxBound)graph.max=tag.getLong("max"+i);if(graph.max<graph.min){long swap=graph.max;graph.max=graph.min;graph.min=swap;}}setChanged();}
    @Override public String computerType(){return "rbmk_graph";}@Override public String[] computerMethods(){return new String[]{"getGraphInfo","getGraphMin","getGraphMax","getGraphAvg","setGraphActive","setGraphPolling","setGraphLabel","setGraphChannel","pushGraphValue"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=2)return new Object[]{switch(method){case "getGraphInfo"->null;case "getGraphMin","getGraphMax"->0L;case "getGraphAvg"->0D;default->false;},"Invalid index (1-2)"};var graph=graphs[index];switch(method){case "getGraphInfo"->{var data=new LinkedHashMap<String,Object>();data.put("active",graph.active);data.put("polling",graph.polling);data.put("label",graph.label);data.put("channel",graph.rtty);data.put("values",graph.values);return new Object[]{data};}case "getGraphMin"->{long min=Long.MAX_VALUE;for(long v:graph.values)if(v<min)min=v;return new Object[]{min==Long.MAX_VALUE?0:min};}case "getGraphMax"->{long max=Long.MIN_VALUE;for(long v:graph.values)if(v>max)max=v;return new Object[]{max==Long.MIN_VALUE?0:max};}case "getGraphAvg"->{long sum=0;int count=0;for(long v:graph.values){sum+=v;count++;}return new Object[]{count>0?(double)sum/count:0D};}case "setGraphActive"->graph.active=args.checkBoolean(1);case "setGraphPolling"->graph.polling=args.checkBoolean(1);case "setGraphLabel"->graph.label=args.checkString(1);case "setGraphChannel"->graph.rtty=args.checkString(1);case "pushGraphValue"->graph.pushValue((long)args.checkInteger(1));default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
