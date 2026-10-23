package com.hbm.machine.reactor.rbmk.panels.gauge;
import com.hbm.api.computer.*;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelEntity;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
/** Source four independent gauge units, current-tick-or-previous radio samples, hold/poll and .1 render smoothing. */
public final class RbmkGaugeEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final GaugeUnit[] gauges=new GaugeUnit[4];
    public RbmkGaugeEntity(BlockPos pos,BlockState state){super(RbmkGaugeContent.ENTITY.get(),pos,state);for(int i=0;i<4;i++)gauges[i]=new GaugeUnit(i);}
    @Override protected void serverTick(ServerLevel level){for(var gauge:gauges)gauge.update();}@Override protected void clientTick(){for(var gauge:gauges){gauge.lastRenderValue=gauge.renderValue;gauge.renderValue+=(gauge.value-gauge.renderValue)*.1;}}
    public final class GaugeUnit{
        public boolean polling,active;public int color;public String label="",rtty="";public long min=0,max=100,value;public double renderValue,lastRenderValue;
        GaugeUnit(int index){color=new int[]{0x800000,0x804000,0x808000,0x000080}[index];label="Gauge "+(index+1);}
        void update(){if(!active||rtty==null||rtty.isEmpty())return;var channel=RTTYSystem.listen(level,rtty);int signal=0;if(channel!=null&&channel.timeStamp<level.getGameTime()-1)channel=null;if(channel!=null&&channel.signal!=null){try{signal=Integer.parseInt(channel.signal.toString());}catch(Exception ignored){}value=signal;}else if(polling)value=0;}
        void write(ByteBuf buffer){buffer.writeBoolean(active).writeBoolean(polling).writeInt(color);string(buffer,label);string(buffer,rtty);buffer.writeLong(min).writeLong(max).writeLong(value);}
        void read(ByteBuf buffer){active=buffer.readBoolean();polling=buffer.readBoolean();color=buffer.readInt();label=string(buffer);rtty=string(buffer);min=buffer.readLong();max=buffer.readLong();value=buffer.readLong();}
        void save(CompoundTag tag,int index){tag.putBoolean("active"+index,active);tag.putBoolean("polling"+index,polling);tag.putInt("color"+index,color);tag.putString("label"+index,label);tag.putString("rtty"+index,rtty);tag.putLong("min"+index,min);tag.putLong("max"+index,max);tag.putLong("value"+index,value);}
        void load(CompoundTag tag,int index){active=tag.getBoolean("active"+index);polling=tag.getBoolean("polling"+index);color=tag.getInt("color"+index);label=tag.getString("label"+index);rtty=tag.getString("rtty"+index);min=tag.getLong("min"+index);max=tag.getLong("max"+index);value=tag.getLong("value"+index);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var gauge:gauges)gauge.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var gauge:gauges)gauge.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<4;i++)gauges[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<4;i++)gauges[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag data){int active=data.getByte("active"),polling=data.getByte("polling");for(int i=0;i<4;i++){var gauge=gauges[i];gauge.active=(active&(1<<i))!=0;gauge.polling=(polling&(1<<i))!=0;gauge.color=Mth.clamp(data.getInt("color"+i),0,0xffffff);gauge.label=data.getString("label"+i);gauge.rtty=data.getString("rtty"+i);gauge.min=data.getInt("min"+i);gauge.max=data.getInt("max"+i);}setChanged();}
    @Override public String computerType(){return "rbmk_gauge";}@Override public String[] computerMethods(){return new String[]{"getGaugeInfo","setGaugeActive","setGaugePolling","setGaugeColor","setGaugeLabel","setGaugeChannel","setGaugeMin","setGaugeMax","setGaugeValue"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=4)return new Object[]{method.equals("getGaugeInfo")?null:false,"Invalid index (1-4)"};var gauge=gauges[index];if(method.equals("getGaugeInfo")){var data=new LinkedHashMap<String,Object>();data.put("active",gauge.active);data.put("polling",gauge.polling);data.put("color",gauge.color);data.put("label",gauge.label);data.put("channel",gauge.rtty);data.put("min",gauge.min);data.put("max",gauge.max);data.put("value",gauge.value);return new Object[]{data};}switch(method){case "setGaugeActive"->gauge.active=args.checkBoolean(1);case "setGaugePolling"->gauge.polling=args.checkBoolean(1);case "setGaugeColor"->gauge.color=Mth.clamp(args.checkInteger(1),0,0xffffff);case "setGaugeLabel"->gauge.label=args.checkString(1);case "setGaugeChannel"->gauge.rtty=args.checkString(1);case "setGaugeMin"->gauge.min=(long)args.checkInteger(1);case "setGaugeMax"->gauge.max=(long)args.checkInteger(1);case "setGaugeValue"->gauge.value=(long)args.checkInteger(1);default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
