package com.hbm.network.rtty.counter;
import com.hbm.api.machine.ICopiable;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.pneumatic.PatternMatcher;
import com.hbm.world.structure.templates.radio.RTTYSystem;
import java.util.Arrays;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Three actual source non-consuming patterns, original ModulePatternMatcher and inventory count broadcasts. */
public final class RadioCounterEntity extends BlockEntity implements WorldlyContainer,MenuProvider,ICopiable,MuffledMachine {
 private final ItemStack[] slots=new ItemStack[3];public final String[] channel={"","",""};public final int[] lastCount=new int[3];private final boolean[] forceUpdate=new boolean[3];public boolean polling,muffled,tilted;public final PatternMatcher matcher=new PatternMatcher(3);private String customName;
 public RadioCounterEntity(BlockPos pos,BlockState state){super(RadioCounterContent.ENTITY.get(),pos,state);Arrays.fill(slots,ItemStack.EMPTY);}
 public static void tick(Level level,BlockPos pos,BlockState state,RadioCounterEntity counter){if(level instanceof ServerLevel server)counter.serverTick(server);}
 private void serverTick(ServerLevel server){var direction=RadioCounterBlock.outward(getBlockState());var support=direction==null?worldPosition:worldPosition.relative(direction.getOpposite());var tile=server.hasChunkAt(support)?server.getBlockEntity(support):null;if(tile instanceof Container inventory){var contents=new ItemStack[inventory.getContainerSize()];for(int j=0;j<contents.length;j++)contents[j]=inventory.getItem(j);for(int i=0;i<3;i++){if(channel[i].isEmpty()||slots[i].isEmpty())continue;int count=0;for(var item:contents)if(!item.isEmpty()&&matcher.isValidForFilter(slots[i],i,item))count+=item.getCount();if(polling||forceUpdate[i]||lastCount[i]!=count){RTTYSystem.broadcast(server,channel[i],count);forceUpdate[i]=false;}lastCount[i]=count;}}
  var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225)player.connection.send(packet);
 }
 public void nextMode(int slot){matcher.nextMode(slots[slot],slot);forceUpdate[slot]=true;setChanged();}
 public void control(CompoundTag data){if(data.contains("polling"))polling=!polling;else for(int i=0;i<3;i++){String name=data.getString("c"+i);if(!name.equals(channel[i])){channel[i]=name;forceUpdate[i]=true;}}if(data.contains("slot")){int slot=data.getInt("slot");if(slot>=0&&slot<3){var stack=ItemStack.parseOptional(level.registryAccess(),data.getCompound("stack"));if(!stack.isEmpty()){setItem(slot,stack.copyWithCount(1));nextMode(slot);}}}setChanged();}
 private void state(CompoundTag tag,HolderLookup.Provider lookup){tag.putBoolean("p",polling);for(int i=0;i<3;i++){tag.putString("c"+i,channel[i]);tag.putInt("l"+i,lastCount[i]);}matcher.write(tag);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);if(customName!=null)tag.putString("name",customName);var list=new ListTag();for(int i=0;i<3;i++)if(!slots[i].isEmpty()){var item=(CompoundTag)slots[i].save(lookup);item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);}
 private void load(CompoundTag tag,HolderLookup.Provider lookup){polling=tag.getBoolean("p");for(int i=0;i<3;i++){channel[i]=tag.getString("c"+i);lastCount[i]=tag.getInt("l"+i);}matcher.read(tag);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");customName=tag.getString("name");Arrays.fill(slots,ItemStack.EMPTY);for(var raw:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)raw;int slot=item.getByte("slot");if(slot>=0&&slot<3)slots[slot]=ItemStack.parseOptional(lookup,item);}}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);state(tag,lookup);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);load(tag,lookup);}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();state(tag,lookup);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){load(tag,lookup);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
 @Override public int getContainerSize(){return 3;}@Override public boolean isEmpty(){return Arrays.stream(slots).allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int slot){return slots[slot];}@Override public ItemStack removeItem(int slot,int amount){var item=slots[slot].split(amount);return item;}@Override public ItemStack removeItemNoUpdate(int slot){var item=slots[slot];slots[slot]=ItemStack.EMPTY;return item;}@Override public void setItem(int slot,ItemStack stack){slots[slot]=stack;stack.limitSize(64);forceUpdate[slot]=true;}@Override public void clearContent(){Arrays.fill(slots,ItemStack.EMPTY);}@Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=144;}
 public int[] getFilterSlots(){return new int[]{0,3};}public void setCustomName(String name){customName=name;setChanged();}
 @Override public Component getDisplayName(){return customName!=null&&!customName.isEmpty()?Component.literal(customName):Component.translatable("container.rttyCounter");}@Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RadioCounterMenu(id,inventory,this);}
 @Override public CompoundTag getSettings(Level world,BlockPos pos){var data=new CompoundTag();var list=new ListTag();for(int i=0;i<3;i++)if(!slots[i].isEmpty()){var item=(CompoundTag)slots[i].save(world.registryAccess());item.putByte("slot",(byte)i);list.add(item);}data.put("items",list);return data;}
 @Override public void pasteSettings(CompoundTag data,int index,Level world,Player player,BlockPos pos){var list=data.getList("items",Tag.TAG_COMPOUND);int count=0;for(int i=0;i<3;i++){if(i<list.size()){var entry=list.getCompound(count);int slot=entry.getByte("slot");boolean router=data.contains("modes")&&slot>index*5&&slot<index*+5;var item=ItemStack.parseOptional(world.registryAccess(),entry);if(!item.isEmpty()&&index<list.size()&&(slot<3||router)&&slot>=0&&slot<3){setItem(slot,item);nextMode(slot);setChanged();}}count++;}}
 @Override public String[] infoForDisplay(Level world,BlockPos pos){return new String[]{"copytool.filter"};}@Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;}
}
