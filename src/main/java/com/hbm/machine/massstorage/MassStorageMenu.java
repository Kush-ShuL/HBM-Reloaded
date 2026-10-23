package com.hbm.machine.massstorage;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original slot coordinates and quick insert/extract, with a nonconsuming one-item pattern slot. */
public final class MassStorageMenu extends AbstractContainerMenu {
    public final MassStorageBlockEntity storage;
    public MassStorageMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,(MassStorageBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));}
    public MassStorageMenu(int id,Inventory inventory,MassStorageBlockEntity storage){
        super(MassStorageContent.MENU.get(),id);this.storage=storage;storage.startOpen(inventory.player);
        addSlot(new Slot(storage,0,61,17));addSlot(new Slot(storage,1,61,53){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}});
        addSlot(new Slot(storage,2,61,89){@Override public boolean mayPlace(ItemStack stack){return false;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,139+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,197));
    }
    @Override public void clicked(int index,int button,ClickType mode,Player player){if(index!=1){super.clicked(index,button,mode,player);return;}if(storage.getStockpile()<=0)getSlot(1).set(getCarried());}
    @Override public boolean canDragTo(Slot slot){return slot.index!=1&&super.canDragTo(slot);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(index==2&&!slot.hasItem())slot.set(storage.quickExtract());if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var original=stack.copy();if(index==0||index==2){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(!storage.quickInsert(stack)&&!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;
    }
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);storage.stopOpen(player);}
}
