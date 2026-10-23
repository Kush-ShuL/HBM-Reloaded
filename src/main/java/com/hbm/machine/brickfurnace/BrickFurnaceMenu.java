package com.hbm.machine.brickfurnace;

import com.hbm.machine.electricfurnace.ElectricFurnaceMenu.SmeltingSlot;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original four machine slots, normal output XP and intentionally no quick-move crafting callback. */
public final class BrickFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public BrickFurnaceMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(4),new SimpleContainerData(6));}
    public BrickFurnaceMenu(int id,Inventory inventory,Container machine,ContainerData data){
        super(BrickFurnaceContent.MENU.get(),id);checkContainerSize(machine,4);checkContainerDataCount(data,6);this.machine=machine;this.data=data;
        addSlot(new Slot(machine,0,62,35));addSlot(new Slot(machine,1,35,17));
        addSlot(new SmeltingSlot(inventory.player,machine,2,116,35));addSlot(new GenericOutputSlot(inventory.player,machine,3,35,53));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));addDataSlots(data);
    }
    private int value(int index){return(data.get(index*2)&0xffff)|(data.get(index*2+1)&0xffff)<<16;}
    public int burnTime(){return value(0);}
    public int maxBurnTime(){return value(1);}
    public int progress(){return value(2);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
        if(index<=3){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}
        else if(BrickFurnaceEntity.fuelTime(stack)<=0){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,1,2,false)&&!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        return original;
    }
}
