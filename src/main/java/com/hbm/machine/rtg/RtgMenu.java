package com.hbm.machine.rtg;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact source slots, with permissive player slots independent of the source's disabled automation. */
public final class RtgMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public RtgMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(15),new SimpleContainerData(7));}
    public RtgMenu(int id,Inventory inventory,Container machine,ContainerData data){
        super(RtgContent.MENU.get(),id);this.machine=machine;this.data=data;checkContainerSize(machine,15);checkContainerDataCount(data,7);
        for(int row=0;row<3;row++)for(int col=0;col<5;col++)addSlot(new Slot(machine,col+row*5,16+col*18,18+row*18){@Override public boolean mayPlace(ItemStack stack){return true;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,106+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,164));addDataSlots(data);
    }
    public long power(){long value=0;for(int i=0;i<4;i++)value|=(data.get(i)&65535L)<<(i*16);return value;}
    public int heat(){return data.get(4);}
    public int maximumHeat(){return Math.max(1,data.get(5));}
    public boolean hasHeat(){return data.get(6)!=0;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<15){if(!moveItemStackTo(stack,15,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,15,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
