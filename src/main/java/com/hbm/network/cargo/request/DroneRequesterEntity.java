package com.hbm.network.cargo.request;
import com.hbm.pneumatic.PatternMatcher;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
public final class DroneRequesterEntity extends RequestContainerEntity implements com.hbm.api.machine.ICopiable {
 public final PatternMatcher matcher=new PatternMatcher(9);private static final int[] ACCESS={9,10,11,12,13,14,15,16,17};public DroneRequesterEntity(BlockPos pos,BlockState state){super(CargoRequestContent.REQUESTER_ENTITY.get(),pos,state,18);}@Override public String sourceName(){return "container.droneRequester";}
 public void serverTick(){networkTick();if(level instanceof ServerLevel server){var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<225)player.connection.send(packet);}}
 public void nextMode(int i){matcher.nextMode(slots.get(i),i);setChanged();}@Override public int[] getSlotsForFace(Direction side){return ACCESS;}@Override public boolean canTakeItemThroughFace(int i,ItemStack stack,Direction side){return true;}
 @Override public RequestNetwork.PathNode createNode(BlockPos pos){var request=new ArrayList<CargoRequestStack>();for(int i=0;i<9;i++){var filter=slots.get(i);var stock=slots.get(i+9);if(filter.isEmpty())continue;String mode=matcher.modes[i];CargoRequestStack match=null;if(PatternMatcher.MODE_EXACT.equals(mode))match=CargoRequestStack.comparable(filter);else if(PatternMatcher.MODE_WILDCARD.equals(mode))match=CargoRequestStack.wildcard(filter);else if(mode!=null)match=CargoRequestStack.tag(mode);if(match==null)continue;if(stock.isEmpty()||!matcher.isValidForFilter(filter,i,stock))request.add(match);}return new RequestNetwork.RequestNode(pos,reachableNodes,request);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);matcher.write(tag);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);matcher.read(tag);}@Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=new CompoundTag();matcher.write(tag);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){matcher.read(tag);}@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}@Override public void onDataPacket(net.minecraft.network.Connection c,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){matcher.read(packet.getTag());}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new CargoRequestMenu(id,inv,this,CargoRequestContent.Kind.REQUESTER);}
 public boolean hasPermission(Player player){return com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(worldPosition.getX()-player.getX(),worldPosition.getY()-player.getY(),worldPosition.getZ()-player.getZ()).lengthVector()<20;}
 public void control(CompoundTag data){if(data.contains("slot")){int slot=data.getInt("slot");if(slot<0||slot>=9)return;var item=ItemStack.parseOptional(level.registryAccess(),data.getCompound("stack"));if(!item.isEmpty()){setItem(slot,item.copyWithCount(1));nextMode(slot);setChanged();}}}
 @Override public CompoundTag getSettings(net.minecraft.world.level.Level world,BlockPos pos){var tag=new CompoundTag();var list=new net.minecraft.nbt.ListTag();for(int i=0;i<9;i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(world.registryAccess());item.putByte("slot",(byte)i);list.add(item);}tag.put("items",list);return tag;}
 @Override public void pasteSettings(CompoundTag data,int index,net.minecraft.world.level.Level world,Player player,BlockPos pos){var list=data.getList("items",net.minecraft.nbt.Tag.TAG_COMPOUND);int count=0;for(int i=0;i<9;i++){if(i<list.size()){var entry=list.getCompound(count);int slot=entry.getByte("slot");var item=ItemStack.parseOptional(world.registryAccess(),entry);boolean router=data.contains("modes")&&slot>index*5&&slot<index*+5;if(!item.isEmpty()&&index<list.size()&&(slot<9||router)&&slot>=0&&slot<9){setItem(slot,item);nextMode(slot);setChanged();}}count++;}}
 @Override public String[] infoForDisplay(net.minecraft.world.level.Level world,BlockPos pos){return new String[]{"copytool.filter"};}

}
