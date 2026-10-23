package com.hbm.machine.reactor.rbmk.panels.numitron;
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
/** Actual two long-value, seven-digit RTTY units with independent zero padding/shortening/bit masks. */
public final class RbmkNumitronEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final DisplayUnit[] displays=new DisplayUnit[2];public RbmkNumitronEntity(BlockPos pos,BlockState state){super(RbmkNumitronContent.ENTITY.get(),pos,state);for(int i=0;i<2;i++)displays[i]=new DisplayUnit(i);}
    @Override protected void serverTick(ServerLevel level){for(var display:displays)display.update();}
    public final class DisplayUnit{
        public boolean polling,active,leading_zeroes=true,shorten_number=true;public String label="",rtty="";public long value,active_digits=0b01111111;
        DisplayUnit(int index){label="Display "+(index+1);}
        void update(){if(!active||rtty==null||rtty.isEmpty())return;var channel=RTTYSystem.listen(level,rtty);long signal=0;if(channel!=null&&channel.timeStamp<level.getGameTime()-1)channel=null;if(channel!=null&&channel.signal!=null){try{signal=Long.parseLong(channel.signal.toString());}catch(Exception ignored){}value=signal;}else if(polling)value=0;}
        void write(ByteBuf buffer){buffer.writeBoolean(shorten_number).writeLong(active_digits).writeBoolean(leading_zeroes).writeBoolean(active).writeBoolean(polling);string(buffer,label);string(buffer,rtty);buffer.writeLong(value);}
        void read(ByteBuf buffer){shorten_number=buffer.readBoolean();active_digits=buffer.readLong();leading_zeroes=buffer.readBoolean();active=buffer.readBoolean();polling=buffer.readBoolean();label=string(buffer);rtty=string(buffer);value=buffer.readLong();}
        void save(CompoundTag tag,int i){tag.putBoolean("shorten_number"+i,shorten_number);tag.putLong("active_digits"+i,active_digits);tag.putBoolean("leading_zeroes"+i,leading_zeroes);tag.putBoolean("active"+i,active);tag.putBoolean("polling"+i,polling);tag.putString("label"+i,label);tag.putString("rtty"+i,rtty);tag.putLong("value"+i,value);}
        void load(CompoundTag tag,int i){shorten_number=tag.getBoolean("shorten_number"+i);active_digits=tag.getLong("active_digits"+i);leading_zeroes=tag.getBoolean("leading_zeroes"+i);active=tag.getBoolean("active"+i);polling=tag.getBoolean("polling"+i);label=tag.getString("label"+i);rtty=tag.getString("rtty"+i);value=tag.getLong("value"+i);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var display:displays)display.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var display:displays)display.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<2;i++)displays[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<2;i++)displays[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag data){int active=data.getByte("active"),polling=data.getByte("polling"),shorten=data.getByte("shorten_number"),leading=data.getByte("leading_zeroes");for(int i=0;i<2;i++){var display=displays[i];display.active=(active&(1<<i))!=0;display.polling=(polling&(1<<i))!=0;display.shorten_number=(shorten&(1<<i))!=0;display.leading_zeroes=(leading&(1<<i))!=0;display.label=data.getString("label"+i);display.rtty=data.getString("rtty"+i);}setChanged();}
    @Override public String computerType(){return "rbmk_numitron";}@Override public String[] computerMethods(){return new String[]{"getDisplayInfo","setDisplayActive","setDisplayPolling","setDisplayLabel","setDisplayChannel","setDisplayValue","setDisplayLeadingZeroes","setDisplayActiveDigits","setDisplayShortenNumber"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=2)return new Object[]{method.equals("getDisplayInfo")?null:false,"Invalid index (1-2)"};var display=displays[index];if(method.equals("getDisplayInfo")){var data=new LinkedHashMap<String,Object>();data.put("shorten_number",display.shorten_number);data.put("active_digits",display.active_digits);data.put("leading_zeroes",display.leading_zeroes);data.put("active",display.active);data.put("polling",display.polling);data.put("label",display.label);data.put("channel",display.rtty);data.put("value",display.value);return new Object[]{data};}switch(method){case "setDisplayActive"->display.active=args.checkBoolean(1);case "setDisplayPolling"->display.polling=args.checkBoolean(1);case "setDisplayLabel"->display.label=args.checkString(1);case "setDisplayChannel"->display.rtty=args.checkString(1);case "setDisplayValue"->display.value=(long)args.checkInteger(1);case "setDisplayLeadingZeroes"->display.leading_zeroes=args.checkBoolean(1);case "setDisplayActiveDigits"->{long mask=args.checkInteger(1);if(mask<0||mask>=128)throw new ComputerException("Invalid value (0-127)");display.active_digits=mask;}case "setDisplayShortenNumber"->display.shorten_number=args.checkBoolean(1);default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
