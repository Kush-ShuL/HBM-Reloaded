package com.hbm.machine.reactor.pwr;

import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original three slots, unfiltered manual placement and source identifier/fuel shift routes. */
public final class PwrMenu extends AbstractContainerMenu {
    private final Container controller;private final BlockPos position;
    public PwrMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private PwrMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof PwrControllerBlockEntity core?core:new SimpleContainer(3),pos);}
    public PwrMenu(int id,Inventory player,Container core,BlockPos pos){super(PwrContent.MENU.get(),id);controller=core;position=pos;checkContainerSize(core,3);addSlot(new Slot(core,0,53,5));addSlot(new GenericOutputSlot(player.player,core,1,89,32));addSlot(new Slot(core,2,8,59));for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+18*column,106+18*row));for(int column=0;column<9;column++)addSlot(new Slot(player,column,8+18*column,164));}
    public BlockPos position(){return position;}
    @Override public boolean stillValid(Player player){return controller.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();boolean moved;if(index<=2)moved=moveItemStackTo(stack,3,slots.size(),true);else{boolean identifier=stack.getItem() instanceof FluidIdentifier;moved=moveItemStackTo(stack,identifier?2:0,identifier?3:1,false);}if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
