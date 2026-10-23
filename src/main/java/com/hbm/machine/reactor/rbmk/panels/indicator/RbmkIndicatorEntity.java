package com.hbm.machine.reactor.rbmk.panels.indicator;
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
/** Six original RTTY lamp ranges, with source inverted interval semantics and independent colors. */
public final class RbmkIndicatorEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final IndicatorUnit[] indicators=new IndicatorUnit[6];public RbmkIndicatorEntity(BlockPos pos,BlockState state){super(RbmkIndicatorContent.ENTITY.get(),pos,state);for(int i=0;i<6;i++)indicators[i]=new IndicatorUnit(i);}
    @Override protected void serverTick(ServerLevel level){for(var indicator:indicators)indicator.update();}
    public final class IndicatorUnit{
        public boolean polling,active,light;public int color;public String label="",rtty="";public long min,max=100;
        IndicatorUnit(int index){color=index%2==0?0xff0000:0xffff00;label="Indicator "+(index+1);}
        void update(){if(!active||rtty==null||rtty.isEmpty())return;var channel=RTTYSystem.listen(level,rtty);if(channel!=null&&channel.timeStamp<level.getGameTime()-1)channel=null;if(channel!=null&&channel.signal!=null){int signal=0;try{signal=Integer.parseInt(channel.signal.toString());}catch(Exception ignored){}decideLight(signal);}else if(polling)decideLight(0);}
        void decideLight(long value){light=min<=max?value>=min&&value<=max:value<max||value>min;}
        void write(ByteBuf buffer){buffer.writeBoolean(active).writeBoolean(polling).writeBoolean(light).writeInt(color);string(buffer,label);string(buffer,rtty);buffer.writeLong(min).writeLong(max);}
        void read(ByteBuf buffer){active=buffer.readBoolean();polling=buffer.readBoolean();light=buffer.readBoolean();color=buffer.readInt();label=string(buffer);rtty=string(buffer);min=buffer.readLong();max=buffer.readLong();}
        void save(CompoundTag tag,int i){tag.putBoolean("active"+i,active);tag.putBoolean("polling"+i,polling);tag.putBoolean("light"+i,light);tag.putInt("color"+i,color);tag.putString("label"+i,label);tag.putString("rtty"+i,rtty);tag.putLong("min"+i,min);tag.putLong("max"+i,max);}
        void load(CompoundTag tag,int i){active=tag.getBoolean("active"+i);polling=tag.getBoolean("polling"+i);light=tag.getBoolean("light"+i);color=tag.getInt("color"+i);label=tag.getString("label"+i);rtty=tag.getString("rtty"+i);min=tag.getLong("min"+i);max=tag.getLong("max"+i);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var indicator:indicators)indicator.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var indicator:indicators)indicator.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<6;i++)indicators[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<6;i++)indicators[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag tag){int active=tag.getByte("active"),polling=tag.getByte("polling");for(int i=0;i<6;i++){var indicator=indicators[i];indicator.active=(active&(1<<i))!=0;indicator.polling=(polling&(1<<i))!=0;indicator.color=Mth.clamp(tag.getInt("color"+i),0,0xffffff);indicator.label=tag.getString("label"+i);indicator.rtty=tag.getString("rtty"+i);indicator.min=tag.contains("min"+i)?tag.getInt("min"+i):Integer.MIN_VALUE;indicator.max=tag.contains("max"+i)?tag.getInt("max"+i):Integer.MAX_VALUE;}setChanged();}
    @Override public String computerType(){return "rbmk_indicator";}@Override public String[] computerMethods(){return new String[]{"getIndicatorInfo","setIndicatorActive","setIndicatorLight","setIndicatorColor","setIndicatorLabel","setIndicatorBounds"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=6)return new Object[]{method.equals("getIndicatorInfo")?null:false,"Invalid index (1-6)"};var indicator=indicators[index];if(method.equals("getIndicatorInfo")){var data=new LinkedHashMap<String,Object>();data.put("active",indicator.active);data.put("polling",indicator.polling);data.put("light",indicator.light);data.put("color",indicator.color);data.put("label",indicator.label);data.put("channel",indicator.rtty);data.put("min",indicator.min);data.put("max",indicator.max);return new Object[]{data};}switch(method){case "setIndicatorActive"->indicator.active=args.checkBoolean(1);case "setIndicatorLight"->indicator.light=args.checkBoolean(1);case "setIndicatorColor"->indicator.color=Mth.clamp(args.checkInteger(1),0,0xffffff);case "setIndicatorLabel"->indicator.label=args.checkString(1);case "setIndicatorBounds"->{indicator.min=(long)args.checkInteger(1);indicator.max=(long)args.checkInteger(2);}default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
