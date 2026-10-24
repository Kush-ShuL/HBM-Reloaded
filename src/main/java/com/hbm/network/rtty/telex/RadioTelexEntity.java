package com.hbm.network.rtty.telex;
import com.hbm.api.computer.*;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original character-at-a-time telex and five-line paper output; unique existing RTTYSystem, not a new transport. */
public final class RadioTelexEntity extends BlockEntity implements ComputerControl,MuffledMachine {
 public static final int LINE_WIDTH=33;public static final char EOL='\n',EOT='\u0004',BELL='\u0007',PRINT='\u000c',PAUSE='\u0016',CLEAR='\u007f';
 public String txChannel="",rxChannel="";public final String[] txBuffer={"","","","",""},rxBuffer={"","","","",""};public int sendingLine,sendingIndex,sendingWait,writingLine;public boolean isSending,printAfterRx,deleteOnReceive=true,muffled,tilted;public char sendingChar=' ';
 public RadioTelexEntity(BlockPos pos,BlockState state){super(RadioTelexContent.ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioTelexEntity telex){if(level instanceof ServerLevel server)telex.serverTick(server);}
 private void serverTick(ServerLevel server){sendingChar=' ';if(isSending&&txChannel.isEmpty())isSending=false;if(isSending){if(sendingWait>0)sendingWait--;else{String line=txBuffer[sendingLine];if(line.length()>sendingIndex){char c=line.charAt(sendingIndex++);if(c==PAUSE)sendingWait=20;else{RTTYSystem.broadcast(server,txChannel,c);sendingChar=c;}}else if(sendingLine>=4){isSending=false;RTTYSystem.broadcast(server,txChannel,EOT);sendingLine=0;sendingIndex=0;}else{RTTYSystem.broadcast(server,txChannel,EOL);sendingLine++;sendingIndex=0;}}}
  if(!rxChannel.isEmpty()){var signal=RTTYSystem.listen(server,rxChannel);if(signal!=null&&signal.signal instanceof Character c&&signal.timeStamp>server.getGameTime()-2&&signal.timeStamp!=-1){if(deleteOnReceive){deleteOnReceive=false;Arrays.fill(rxBuffer,"");writingLine=0;}if(c==EOT){if(printAfterRx){printAfterRx=false;print();}deleteOnReceive=true;}else if(c==EOL){if(writingLine<4)writingLine++;setChanged();}else if(c==BELL)server.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),SoundEvents.EXPERIENCE_ORB_PICKUP,SoundSource.BLOCKS,2F,.5F);else if(c==PRINT)printAfterRx=true;else if(c==CLEAR){Arrays.fill(rxBuffer,"");writingLine=0;}else{rxBuffer[writingLine]+=c;setChanged();}}}
  var packet=getUpdatePacket();try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<256)player.connection.send(packet);dispatchScope.commit();}
 }
 public void control(CompoundTag data){for(int i=0;i<5;i++)if(data.contains("tx"+i))txBuffer[i]=data.getString("tx"+i);String command=data.getString("cmd");if(command.equals("snd")&&!isSending){isSending=true;sendingLine=0;sendingIndex=0;}if(command.equals("rxprt"))print();if(command.equals("rxcls")){Arrays.fill(rxBuffer,"");writingLine=0;}if(command.equals("sve")){txChannel=data.getString("txChan");rxChannel=data.getString("rxChan");}setChanged();}
 public void print(){if(!(level instanceof ServerLevel server))return;var paper=new ItemStack(Items.PAPER);var lines=new ArrayList<Component>();for(String line:rxBuffer)if(!line.isEmpty())lines.add(Component.literal("\u00a7r\u00a77"+line));paper.set(DataComponents.LORE,new ItemLore(lines));paper.set(DataComponents.CUSTOM_NAME,Component.translatable("item.hbm.telex_message"));server.addFreshEntity(new ItemEntity(server,worldPosition.getX()+.5,worldPosition.getY()+1,worldPosition.getZ()+.5,paper));}
 private void save(CompoundTag tag){for(int i=0;i<5;i++){tag.putString("tx"+i,txBuffer[i]);tag.putString("rx"+i,rxBuffer[i]);}tag.putString("txChan",txChannel);tag.putString("rxChan",rxChannel);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
 private void load(CompoundTag tag){for(int i=0;i<5;i++){txBuffer[i]=tag.getString("tx"+i);rxBuffer[i]=tag.getString("rx"+i);}txChannel=tag.getString("txChan");rxChannel=tag.getString("rxChan");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);save(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);load(tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();save(tag);tag.putInt("hbm_sending_char",sendingChar);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){load(tag);sendingChar=(char)tag.getInt("hbm_sending_char");}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public String computerType(){return "ntm_telex";}@Override public String[] computerMethods(){return new String[]{"getChannels","setChannels","getSendingTexts","getReceivingText","setSendingText","printMessage","clearAll","sendMessage"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){switch(method){case "getChannels"->{return new Object[]{txChannel,rxChannel};}case "setChannels"->{String[] old={txChannel,rxChannel};rxChannel=args.checkString(0);txChannel=args.checkString(1);setChanged();return old;}case "getSendingTexts"->{return txBuffer.clone();}case "getReceivingText"->{return rxBuffer.clone();}case "setSendingText"->{for(int i=0;i<5;i++){String value=args.get(i)==null?"":args.checkString(i);txBuffer[i]=value.length()>LINE_WIDTH?value.substring(0,LINE_WIDTH):value;}setChanged();return new Object[]{true};}case "printMessage"->print();case "clearAll"->{Arrays.fill(rxBuffer,"");writingLine=0;setChanged();}case "sendMessage"->{if(isSending)return new Object[]{false};isSending=true;sendingLine=0;sendingIndex=0;return new Object[]{true};}default->throw new ComputerException("No such method: "+method);}return new Object[0];}
 @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
