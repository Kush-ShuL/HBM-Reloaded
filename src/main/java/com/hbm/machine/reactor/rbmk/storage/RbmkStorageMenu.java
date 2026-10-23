package com.hbm.machine.reactor.rbmk.storage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source deliberately uses ordinary unrestricted Slot, unlike its automated isItemValid restriction. */
public final class RbmkStorageMenu extends AbstractContainerMenu {
    public final BlockPos position;
    public RbmkStorageMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,new SimpleContainer(12),data.readBlockPos());}
    public RbmkStorageMenu(int id,Inventory player,Container machine,BlockPos pos){super(RbmkStorageContent.MENU.get(),id);position=pos;for(int i=0;i<3;i++)for(int j=0;j<4;j++)addSlot(new Slot(machine,i+j*3,32+j*32,29+i*16));for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(player,9+j+i*9,8+j*18,104+i*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,162));}
    @Override public boolean stillValid(Player player){return true;}@Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<12){if(!moveItemStackTo(stack,12,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,12,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
