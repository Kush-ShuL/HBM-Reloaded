package com.hbm.machine.heatingoven;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source ContainerFirebox slots and shift transfer, used by the actual heatingoven. */
public final class HeatingOvenMenu extends AbstractContainerMenu {
 private final Container machine;public final BlockPos position;
 public HeatingOvenMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}private HeatingOvenMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof HeatingOvenBlockEntity machine?machine:new SimpleContainer(2),pos);}
 public HeatingOvenMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(HeatingOvenContent.MENU.get(),id);this.machine=machine;position=pos;machine.startOpen(inventory.player);addSlot(new Slot(machine,0,44,27));addSlot(new Slot(machine,1,62,27));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,86+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,144));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}@Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<2){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,2,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
