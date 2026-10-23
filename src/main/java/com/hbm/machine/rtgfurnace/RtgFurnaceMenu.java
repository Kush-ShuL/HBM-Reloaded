package com.hbm.machine.rtgfurnace;

import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original five machine slots, permissive three pellet slots and unfiltered player shift transfer. */
public final class RtgFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public RtgFurnaceMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(5),new SimpleContainerData(1));}
    public RtgFurnaceMenu(int id,Inventory inventory,Container machine,ContainerData data){
        super(RtgFurnaceContent.MENU.get(),id);checkContainerSize(machine,5);checkContainerDataCount(data,1);this.machine=machine;this.data=data;
        addSlot(new Slot(machine,0,56,17));addSlot(new Slot(machine,1,38,53));addSlot(new Slot(machine,2,56,53));addSlot(new Slot(machine,3,74,53));
        addSlot(new GenericOutputSlot(inventory.player,machine,4,116,35));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));addDataSlots(data);
    }
    public int progress(){return data.get(0);}
    public boolean hasPower(){return RtgFurnaceEntity.hasHeat(machine);}
    public int scaledProgress(int width){return progress()*width/RtgFurnaceEntity.PROCESSING_SPEED;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
        if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,4,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        // Original transferStackInSlot does not call onPickupFromSlot/onCrafting here.
        return original;
    }
}
