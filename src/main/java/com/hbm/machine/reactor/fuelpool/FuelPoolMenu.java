package com.hbm.machine.reactor.fuelpool;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source exact slot coordinates; manual insertion deliberately uses plain, unrestricted Slot. */
public final class FuelPoolMenu extends AbstractContainerMenu {
    public final FuelPoolBlockEntity pool;
    public FuelPoolMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,(FuelPoolBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));}
    public FuelPoolMenu(int id,Inventory inventory,FuelPoolBlockEntity pool){super(FuelPoolContent.MENU.get(),id);this.pool=pool;int[][] positions={{71,21},{89,21},{53,39},{71,39},{89,39},{107,39},{53,57},{71,57},{89,57},{107,57},{71,75},{89,75}};for(int i=0;i<positions.length;i++)addSlot(new Slot(pool,i,positions[i][0],positions[i][1]){@Override public boolean mayPlace(ItemStack stack){return true;}});for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,107+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,165));}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<12){if(!moveItemStackTo(stack,12,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,12,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();if(stack.getCount()==original.getCount())return ItemStack.EMPTY;slot.onTake(player,stack);return original;}
    @Override public boolean stillValid(Player player){return pool.stillValid(player);}
}
