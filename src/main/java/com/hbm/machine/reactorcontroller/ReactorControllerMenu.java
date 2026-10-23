package com.hbm.machine.reactorcontroller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** One unrestricted manual slot; source denies all sided automation and has a12-block usable radius. */
public final class ReactorControllerMenu extends AbstractContainerMenu {
    private final Container machine;private final BlockPos pos;
    public ReactorControllerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private ReactorControllerMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ReactorControllerEntity be?be:new SimpleContainer(1),pos);}
    public ReactorControllerMenu(int id,Inventory player,Container machine,BlockPos pos){super(ReactorControllerContent.MENU.get(),id);this.machine=machine;this.pos=pos.immutable();checkContainerSize(machine,1);addSlot(new Slot(machine,0,92,38));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+18*col,84+18*row));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+18*col,142));}
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=0){if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
