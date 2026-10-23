package com.hbm.machine.reactor.rbmk.panels.keypad;
import com.hbm.api.computer.*;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelEntity;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
/** Four actual source RTTY buttons, persistent polling latch and7-counter post-decrement momentary press. */
public final class RbmkKeypadEntity extends RbmkRadioPanelEntity implements ComputerControl{
    public final KeyUnit[] keys=new KeyUnit[4];public RbmkKeypadEntity(BlockPos pos,BlockState state){super(RbmkKeypadContent.ENTITY.get(),pos,state);for(int i=0;i<4;i++)keys[i]=new KeyUnit(i);}
    @Override protected void serverTick(ServerLevel level){for(var key:keys)key.update();}
    public final class KeyUnit{
        public boolean polling,isPressed,active;public int color,clickTimer;public String label="",rtty="",command="";
        KeyUnit(int index){color=new int[]{0xff0000,0xffff00,0x0080ff,0x00ff00}[index];label="Button "+(index+1);}
        public void click(){if(!active)return;if(!polling){if(canSend())RTTYSystem.broadcast(level,rtty,command);isPressed=true;clickTimer=7;}else{isPressed=!isPressed;setChanged();}level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,SoundEvents.UI_BUTTON_CLICK.value(),SoundSource.BLOCKS,1,isPressed?1:.75F);}
        void update(){if(!active)return;if(polling&&isPressed&&canSend())RTTYSystem.broadcast(level,rtty,command);if(!polling&&isPressed&&clickTimer--<=0)isPressed=false;}
        boolean canSend(){return rtty!=null&&!rtty.isEmpty()&&command!=null&&!command.isEmpty();}
        void write(ByteBuf buffer){buffer.writeBoolean(active).writeBoolean(polling).writeBoolean(isPressed).writeInt(color);string(buffer,label);string(buffer,rtty);string(buffer,command);}
        void read(ByteBuf buffer){active=buffer.readBoolean();polling=buffer.readBoolean();isPressed=buffer.readBoolean();color=buffer.readInt();label=string(buffer);rtty=string(buffer);command=string(buffer);}
        void save(CompoundTag tag,int index){tag.putBoolean("active"+index,active);tag.putBoolean("polling"+index,polling);tag.putBoolean("isPressed"+index,isPressed);tag.putInt("color"+index,color);tag.putString("label"+index,label);tag.putString("rtty"+index,rtty);tag.putString("command"+index,command);}
        void load(CompoundTag tag,int index){active=tag.getBoolean("active"+index);polling=tag.getBoolean("polling"+index);isPressed=tag.getBoolean("isPressed"+index);color=tag.getInt("color"+index);label=tag.getString("label"+index);rtty=tag.getString("rtty"+index);command=tag.getString("command"+index);}
    }
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);for(var key:keys)key.write(buffer);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);for(var key:keys)key.read(buffer);}@Override protected void savePanel(CompoundTag tag){super.savePanel(tag);for(int i=0;i<4;i++)keys[i].save(tag,i);}@Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);for(int i=0;i<4;i++)keys[i].load(tag,i);}
    @Override public void receiveControl(Player player,CompoundTag data){int active=data.getByte("active"),polling=data.getByte("polling");for(int i=0;i<4;i++){var key=keys[i];key.active=(active&(1<<i))!=0;key.polling=(polling&(1<<i))!=0;key.color=Mth.clamp(data.getInt("color"+i),0,0xffffff);key.label=data.getString("label"+i);key.rtty=data.getString("rtty"+i);key.command=data.getString("cmd"+i);}setChanged();}
    @Override public String computerType(){return "rbmk_keypad";}@Override public String[] computerMethods(){return new String[]{"getKeyInfo","setKeyActive","setKeyPolling","setKeyColor","setKeyLabel","setKeyChannel","setKeyCommand","pressKey","getKeyPressed"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{int index=args.checkInteger(0)-1;if(index<0||index>=4)return new Object[]{method.equals("getKeyInfo")?null:false,"Invalid index (1-4)"};var key=keys[index];if(method.equals("getKeyInfo")){var data=new LinkedHashMap<String,Object>();data.put("active",key.active);data.put("polling",key.polling);data.put("pressed",key.isPressed);data.put("color",key.color);data.put("label",key.label);data.put("channel",key.rtty);data.put("command",key.command);return new Object[]{data};}if(method.equals("getKeyPressed"))return new Object[]{key.isPressed};switch(method){case "setKeyActive"->key.active=args.checkBoolean(1);case "setKeyPolling"->key.polling=args.checkBoolean(1);case "setKeyColor"->key.color=Mth.clamp(args.checkInteger(1),0,0xffffff);case "setKeyLabel"->key.label=args.checkString(1);case "setKeyChannel"->key.rtty=args.checkString(1);case "setKeyCommand"->key.command=args.checkString(1);case "pressKey"->{if(!key.active)return new Object[]{false,"Key is not active"};key.click();}default->throw new ComputerException("No such method: "+method);}setChanged();return new Object[]{true};}
}
