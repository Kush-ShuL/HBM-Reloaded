package com.hbm.bomb.nuclear.programmable;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source ContainerNukeFstbmb slot coordinates, source withdraw-only transfer and authoritative timer controls. */
public final class TimedBalefireMenu extends AbstractContainerMenu {
 public final Container bomb;private final ContainerData data;
 public TimedBalefireMenu(int id,Inventory inventory,net.minecraft.network.RegistryFriendlyByteBuf buffer){this(id,inventory,(TimedBalefireBlockEntity)inventory.player.level().getBlockEntity(buffer.readBlockPos()));}
 private TimedBalefireMenu(int id,Inventory inventory,TimedBalefireBlockEntity bomb){this(id,inventory,bomb,bomb.data);}
 public TimedBalefireMenu(int id,Inventory inventory,Container bomb,ContainerData data){super(ProgrammableNuclearContent.TIMED_MENU.get(),id);this.bomb=bomb;this.data=data;checkContainerSize(bomb,2);checkContainerDataCount(data,3);addSlot(new Slot(bomb,0,17,36));addSlot(new Slot(bomb,1,53,36));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,140+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,198));addDataSlots(data);}
 public int timer(){return data.get(0);}public boolean started(){return data.get(1)!=0;}public boolean loaded(){return data.get(2)!=0;}
 @Override public boolean stillValid(Player player){return bomb.stillValid(player);}
 @Override public boolean clickMenuButton(Player player,int value){return bomb instanceof TimedBalefireBlockEntity block&&block.button(player,value);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>2)return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return before;}
}
