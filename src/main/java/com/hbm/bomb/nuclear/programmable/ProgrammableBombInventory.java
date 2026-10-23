package com.hbm.bomb.nuclear.programmable;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Original machine/custom inventory persistence and manual slots; neither bomb exposes sided automation. */
public abstract class ProgrammableBombInventory extends BlockEntity implements WorldlyContainer,MenuProvider {
 protected final NonNullList<ItemStack> slots;
 private final String name;private final double useRange;private String customName;
 protected ProgrammableBombInventory(BlockEntityType<?> type,BlockPos pos,BlockState state,int size,String name,double useRange){super(type,pos,state);slots=NonNullList.withSize(size,ItemStack.EMPTY);this.name=name;this.useRange=useRange;}
 @Override public int getContainerSize(){return slots.size();}
 @Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
 @Override public ItemStack getItem(int index){return slots.get(index);}
 @Override public ItemStack removeItem(int index,int amount){ItemStack stack=slots.get(index),result=stack.getCount()<=amount?removeItemNoUpdate(index):stack.split(amount);setChanged();return result;}
 @Override public ItemStack removeItemNoUpdate(int index){ItemStack stack=slots.get(index);slots.set(index,ItemStack.EMPTY);return stack;}
 @Override public void setItem(int index,ItemStack stack){slots.set(index,stack);if(stack.getCount()>64)stack.setCount(64);setChanged();}
 @Override public void clearContent(){slots.clear();setChanged();}
 @Override public boolean stillValid(Player player){return level!=null&&level.getBlockEntity(worldPosition)==this&&player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=useRange*useRange;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
 @Override public int[] getSlotsForFace(Direction side){return new int[0];}
 @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
 @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
 public void setCustomName(String value){customName=value;setChanged();}
 @Override public Component getDisplayName(){return customName!=null&&!customName.isEmpty()?Component.literal(customName):Component.translatable(name);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ListTag items=new ListTag();for(int i=0;i<slots.size();i++)if(!slots.get(i).isEmpty()){CompoundTag item=(CompoundTag)slots.get(i).save(lookup);item.putByte("slot",(byte)i);items.add(item);}tag.put("items",items);if(customName!=null)tag.putString("name",customName);}
 @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);slots.clear();ListTag items=tag.getList("items",Tag.TAG_COMPOUND);for(int i=0;i<items.size();i++){CompoundTag item=items.getCompound(i);int slot=item.getByte("slot");if(slot>=0&&slot<slots.size())slots.set(slot,ItemStack.parseOptional(lookup,item));}customName=tag.getString("name");}
}
