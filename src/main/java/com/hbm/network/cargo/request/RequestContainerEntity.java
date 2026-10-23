package com.hbm.network.cargo.request;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
/** Original separate request-network inventory base,128distance and lowercaseitems/byteslot. */
public abstract class RequestContainerEntity extends RequestNetworkEntity implements WorldlyContainer,MenuProvider {
 public final NonNullList<ItemStack> slots;private String customName="";
 protected RequestContainerEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,int count){super(type,pos,state);slots=NonNullList.withSize(count,ItemStack.EMPTY);}
 public abstract String sourceName();@Override public Component getDisplayName(){return customName.isEmpty()?Component.translatable(sourceName()):Component.literal(customName);}public void setCustomName(String name){customName=name;setChanged();}
 @Override public int getContainerSize(){return slots.size();}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}@Override public ItemStack getItem(int i){return slots.get(i);}@Override public ItemStack removeItem(int i,int amount){var stack=ContainerHelper.removeItem(slots,i,amount);if(!stack.isEmpty())setChanged();return stack;}@Override public ItemStack removeItemNoUpdate(int i){return ContainerHelper.takeItem(slots,i);}@Override public void setItem(int i,ItemStack stack){slots.set(i,stack);stack.limitSize(64);setChanged();}@Override public void clearContent(){slots.clear();setChanged();}@Override public int getMaxStackSize(){return 64;}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=128;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}@Override public boolean canPlaceItemThroughFace(int i,ItemStack stack,Direction side){return canPlaceItem(i,stack);}@Override public boolean canTakeItemThroughFace(int i,ItemStack stack,Direction side){return false;}@Override public int[] getSlotsForFace(Direction side){return new int[0];}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);var items=new ListTag();for(int i=0;i<slots.size();i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);items.add(item);}tag.put("items",items);if(!customName.isEmpty())tag.putString("name",customName);}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);slots.clear();for(var raw:tag.getList("items",Tag.TAG_COMPOUND)){var item=(CompoundTag)raw;int i=item.getByte("slot")&255;if(i<slots.size())slots.set(i,ItemStack.parseOptional(lookup,item));}customName=tag.getString("name");}
}
