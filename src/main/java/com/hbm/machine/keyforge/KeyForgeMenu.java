package com.hbm.machine.keyforge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class KeyForgeMenu extends AbstractContainerMenu {
    private final Container machine;
    private final BlockPos position;
    public KeyForgeMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,new SimpleContainer(3),data.readBlockPos());}
    public KeyForgeMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(KeyForgeContent.MENU.get(),id);this.machine=machine;position=pos;
        for(int i=0;i<3;i++)addSlot(new Slot(machine,i,44+i*36,36));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,104+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,162));
    }
    public BlockPos position(){return position;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
        // Source intentionally treats only slot0 as the outward shift-click range.
        if(index<=0){if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}
