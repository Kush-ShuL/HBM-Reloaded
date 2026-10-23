package com.hbm.machine.supercomputer;
import com.hbm.item.battery.*;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Actual8machine+36player slots, strict SlotNonRetarded input and realCraftingOutput callbacks. */
public final class SuperComputerMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public SuperComputerMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}private SuperComputerMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof SuperComputerEntity computer?computer:new SimpleContainer(8),pos);}
 public SuperComputerMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(SuperComputerContent.MENU.get(),id);this.machine=machine;position=pos;machine.startOpen(inventory.player);int[][] coords={{152,81},{35,80},{8,27},{26,27},{44,27}};for(int i=0;i<5;i++){int slot=i;addSlot(new Slot(machine,i,coords[i][0],coords[i][1]){@Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(slot,stack);}@Override public int getMaxStackSize(){return Math.max(machine.getMaxStackSize(),hasItem()?getItem().getCount():1);}});}for(int i=0;i<3;i++)addSlot(new GenericOutputSlot(inventory.player,machine,5+i,80+i*18,27));for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,8+i%9*18,129+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,187));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<8){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,8,slots.size(),true))return ItemStack.EMPTY;}else{int start=stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem?0:stack.is(Blueprints.ITEM.get())?1:2;int end=start==2?5:start+1;if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
