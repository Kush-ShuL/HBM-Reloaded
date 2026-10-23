package com.hbm.machine.exposurechamber;
import com.hbm.item.battery.*;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source seven visible slots; the zero-count particle identity is never a GUI slot. */
public final class ExposureChamberMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public ExposureChamberMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
 private ExposureChamberMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ExposureChamberEntity chamber?chamber:new SimpleContainer(8),pos);}
 public ExposureChamberMenu(int id,Inventory player,Container machine,BlockPos pos){super(ExposureChamberContent.MENU.get(),id);checkContainerSize(machine,8);this.machine=machine;position=pos;machine.startOpen(player.player);addSlot(new Slot(machine,0,8,18));addSlot(new Slot(machine,2,8,54){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,3,80,36));addSlot(new GenericOutputSlot(player.player,machine,4,116,36));addSlot(new Slot(machine,5,152,54));addSlot(new Slot(machine,6,44,54));addSlot(new Slot(machine,7,62,54));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,104+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,162));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=6){if(!moveItemStackTo(stack,7,slots.size(),true))return ItemStack.EMPTY;}else if(ExposureChamberContent.isSourceMachineUpgrade(stack)){if(!moveItemStackTo(stack,5,7,false))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(stack.getCount()==original.getCount())return ItemStack.EMPTY;return original;}
}
