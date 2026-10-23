package com.hbm.machine.reactor.rbmk.panels.terminal;
import api.hbm.redstoneoverradio.IRORInteractive;
import com.hbm.api.computer.*;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.machine.reactor.rbmk.panels.RbmkRadioPanelEntity;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
/** Real source input/RTTY/history/CC terminal; one state machine and original persisted legacy mode key. */
public final class RbmkTerminalEntity extends RbmkRadioPanelEntity implements ComputerControl,IRORInteractive{
    public final String[] history=new String[17];public String channel="",repeatCmd="";public boolean doesRepeat,ocMode;
    public RbmkTerminalEntity(BlockPos pos,BlockState state){super(RbmkTerminalContent.ENTITY.get(),pos,state);}
    @Override protected int syncRadius(){return ocMode?10:50;}
    @Override protected void serverTick(ServerLevel level){if(ocMode)return;if(!channel.isEmpty()&&!repeatCmd.isEmpty())RTTYSystem.broadcast(level,channel,repeatCmd+"");}
    public void eval(String command){if(command==null)return;if(ocMode){push(command);setChanged();return;}push(command);if(command.isEmpty())return;if(command.startsWith("chan ")){channel=command.substring(5);push("Set channel to "+(channel.isEmpty()?"<none>":channel));setChanged();return;}if(command.equals("chan")){channel="";push("Set channel to <none>");setChanged();return;}if(command.startsWith("start ")){repeatCmd=command.substring(6);push("Repeating signal on "+channel);setChanged();return;}if(command.equals("stop")){repeatCmd="";push("Stopping repeat signal");setChanged();return;}if(command.startsWith("send ")){if(channel.isEmpty()){push("Cannot send - no channel set");return;}RTTYSystem.broadcast(level,channel,command.substring(5));push("Sent signal on "+channel);return;}if(command.equals("horse")){push("Horse.");return;}if(command.equals("selfdestruct")){level.destroyBlock(worldPosition,false);var explosion=new ExplosionVNT(level,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,5,null);explosion.setEntityProcessor(new EntityProcessorCrossSmooth(1,50).setupPiercing(5F,.5F));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.setSFX(new ExplosionEffectWeapon(10,2.5F,1));explosion.explode();return;}if(command.equals("clear")){Arrays.fill(history,"");return;}push("Unrecognized command!");}
    // Preserve source indexing: push shifts slots15..1; slot16 remains available to ROR set17.
    public void push(String text){for(int i=history.length-2;i>0;i--)history[i]=history[i-1];history[0]=text;}
    @Override protected void writeWire(ByteBuf buffer){super.writeWire(buffer);buffer.writeBoolean(!repeatCmd.isEmpty());for(String line:history)string(buffer,line);}@Override protected void readWire(ByteBuf buffer){super.readWire(buffer);doesRepeat=buffer.readBoolean();for(int i=0;i<history.length;i++)history[i]=string(buffer);}
    @Override protected void savePanel(CompoundTag tag){super.savePanel(tag);tag.putString("channel",channel.isEmpty()?" ":channel);tag.putString("repeatCmd",repeatCmd.isEmpty()?" ":repeatCmd);tag.putBoolean("ocMode",ocMode);for(int i=0;i<history.length;i++)tag.putString("history"+i,history[i]==null||history[i].isEmpty()?" ":history[i]);}
    @Override protected void loadPanel(CompoundTag tag){super.loadPanel(tag);channel=tag.getString("channel");if(channel.equals(" "))channel="";repeatCmd=tag.getString("repeatCmd");if(repeatCmd.equals(" "))repeatCmd="";ocMode=tag.getBoolean("ocMode");for(int i=0;i<history.length;i++){history[i]=tag.getString("history"+i);if(history[i].equals(" "))history[i]="";}}
    @Override public void receiveControl(Player player,CompoundTag tag){if(tag.contains("cmd")){eval(tag.getString("cmd"));setChanged();}}
    @Override public String computerType(){return "rbmk_terminal";}@Override public String[] computerMethods(){return new String[]{"enableOCMode","isOCMode","write","writeln","readInput","getAllHistory","clearScreen","enableCCMode","isCCMode"};}
    @Override public Object[] callComputer(String method,ComputerArguments args)throws ComputerException{if(method.equals("enableOCMode")||method.equals("enableCCMode")){ocMode=args.checkBoolean(0);if(ocMode){Arrays.fill(history,"");push("CC MODE ENABLED");push("Terminal ready.");}setChanged();return new Object[]{true};}if(method.equals("isOCMode")||method.equals("isCCMode"))return new Object[]{ocMode};if(!ocMode)return new Object[]{switch(method){case "readInput"->"";case "getAllHistory"->null;default->false;},"CC mode not enabled"};switch(method){case "write","writeln"->{push(args.checkString(0));setChanged();return new Object[]{true};}case "readInput"->{return new Object[]{history[0]!=null?history[0]:""};}case "getAllHistory"->{String[] copy=new String[history.length];for(int i=0;i<copy.length;i++)copy[i]=history[i]!=null?history[i]:"";return new Object[]{copy};}case "clearScreen"->{Arrays.fill(history,"");setChanged();return new Object[]{true};}default->throw new ComputerException("No such method: "+method);}}
    @Override public String[] getFunctionInfo(){return new String[]{PREFIX_FUNCTION+"clear",PREFIX_FUNCTION+"write"+NAME_SEPARATOR+"text",PREFIX_FUNCTION+"set<line#>"+NAME_SEPARATOR+"text",PREFIX_FUNCTION+"submit"+NAME_SEPARATOR+"command"};}
    @Override public String runRORFunction(String name,String[] params){if((PREFIX_FUNCTION+"clear").equals(name)){Arrays.fill(history,"");setChanged();return null;}String allParams=String.join(" ",params);if((PREFIX_FUNCTION+"write").equals(name)){push(allParams);setChanged();return null;}if(name.startsWith(PREFIX_FUNCTION+"set")){int line=IRORInteractive.parseInt(name.substring(PREFIX_FUNCTION.length()+3),1,17)-1;history[line]=allParams;setChanged();return null;}if((PREFIX_FUNCTION+"submit").equals(name)){eval(allParams);return null;}return null;}
}
