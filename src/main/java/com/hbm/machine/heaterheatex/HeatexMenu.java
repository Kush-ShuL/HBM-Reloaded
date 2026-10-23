package com.hbm.machine.heaterheatex;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original single manually accessible identifier slot; no item transfer or automation added. */
public final class HeatexMenu extends AbstractContainerMenu {
 private final Container machine;public final BlockPos position;
 public HeatexMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}private HeatexMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof HeatexBlockEntity heater?heater:new SimpleContainer(1),pos);}
 public HeatexMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(HeatexContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,80,72));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,180));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index==0){if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
 public void control(int field,int value){if(machine instanceof HeatexBlockEntity heater)heater.receiveControl(field,value);}
}
