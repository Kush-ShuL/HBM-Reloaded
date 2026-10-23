package com.hbm.conveyor.crane;

import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.item.ItemStack;

/** Sole original CraneInserter.addToInventory: mutable input, existing stacks before empty slots. */
public final class CraneInventoryTransfer {
    public static ItemStack addToInventory(Container inventory,int[] access,ItemStack toAdd,Direction side){
        if(toAdd==null||toAdd.isEmpty())return ItemStack.EMPTY;
        WorldlyContainer sided=inventory instanceof WorldlyContainer value?value:null;int limit=inventory.getMaxStackSize(),size=access==null?inventory.getContainerSize():access.length;
        for(int i=0;i<size;i++){
            int slot=access==null?i:access[i];var current=inventory.getItem(slot);
            if(!current.isEmpty()&&ItemStack.isSameItemSameComponents(toAdd,current)&&current.getCount()<Math.min(current.getMaxStackSize(),limit)&&(sided==null||sided.canPlaceItemThroughFace(slot,toAdd,side))&&inventory.canPlaceItem(slot,toAdd)){
                int amount=Math.min(toAdd.getCount(),Math.min(current.getMaxStackSize(),limit)-current.getCount());current.grow(amount);toAdd.shrink(amount);inventory.setChanged();if(toAdd.isEmpty())return ItemStack.EMPTY;
            }
        }
        for(int i=0;i<size;i++){
            int slot=access==null?i:access[i];if(inventory.getItem(slot).isEmpty()&&(sided==null||sided.canPlaceItemThroughFace(slot,toAdd,side))&&inventory.canPlaceItem(slot,toAdd)){
                // Source empty-slot insertion is limited by the inventory alone, not the item's max stack.
                int amount=Math.min(toAdd.getCount(),limit);inventory.setItem(slot,toAdd.copyWithCount(amount));toAdd.shrink(amount);inventory.setChanged();if(toAdd.isEmpty())return ItemStack.EMPTY;
            }
        }
        return toAdd;
    }
    /** InventoryUtil's distinct array overload: merge item maxima, then place the entire remainder in the first empty slot. */
    public static ItemStack addToBuffer(ItemStack[] inventory,int start,int end,ItemStack remaining){
        if(remaining.isEmpty())return ItemStack.EMPTY;
        for(int i=start;i<=end;i++)if(!inventory[i].isEmpty()&&ItemStack.isSameItemSameComponents(inventory[i],remaining)){
            int count=Math.min(remaining.getCount(),inventory[i].getMaxStackSize()-inventory[i].getCount());
            if(count>0){inventory[i].grow(count);remaining.shrink(count);if(remaining.isEmpty())return ItemStack.EMPTY;}
        }
        for(int i=start;i<=end;i++)if(inventory[i].isEmpty()){inventory[i]=remaining;return ItemStack.EMPTY;}
        return remaining;
    }
    /** Original InventoryUtil.masquerade for inserting into a sided inventory. */
    public static int[] access(Container inventory,Direction side){
        if(!(inventory instanceof WorldlyContainer sided))return null;
        if(inventory instanceof net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity)return new int[]{1,0};
        if(inventory instanceof com.hbm.machine.brickfurnace.BrickFurnaceEntity)return new int[]{1,0,3};
        return sided.getSlotsForFace(side);
    }
    private CraneInventoryTransfer(){}
}
