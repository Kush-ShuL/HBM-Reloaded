package com.hbm.item.containment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original ContainerLeadBox: exact 5x4 grid, shift ranges, selected-item lock and per-cell limit. */
public final class ContainmentBoxMenu extends AbstractContainerMenu {
    public final ContainmentBoxInventory box;
    private final Inventory inventory;
    private final InteractionHand hand;
    public ContainmentBoxMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readEnum(InteractionHand.class));}
    public ContainmentBoxMenu(int id,Inventory inventory,InteractionHand hand){
        super(ContainmentBoxContent.MENU.get(),id);this.inventory=inventory;this.hand=hand;
        box=new ContainmentBoxInventory(inventory.player,inventory.player.getItemInHand(hand));box.startOpen(inventory.player);
        for(int y=0;y<4;y++)for(int x=0;x<5;x++)addSlot(new Slot(box,x+y*5,43+x*18,18+y*18){
            @Override public boolean mayPlace(ItemStack item){return box.canPlaceItem(getContainerSlot(),item);}
            @Override public int getMaxStackSize(){return Math.max(box.getMaxStackSize(),hasItem()?getItem().getCount():1);}
        });
        for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,104+y*18));
        for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,162));
    }
    @Override public boolean stillValid(Player player){return box.stillValid(player);}
    @Override public void clicked(int index,int button,ClickType type,Player player){
        if(type==ClickType.SWAP&&(hand==InteractionHand.MAIN_HAND?button==inventory.selected:button==40))return;
        if(hand==InteractionHand.MAIN_HAND&&index==inventory.selected+47)return;
        super.clicked(index,button,type,player);
    }
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var item=slot.getItem();var copy=item.copy();
        if(index<20){if(!moveItemStackTo(item,20,slots.size(),true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(item,0,20,false))return ItemStack.EMPTY;
        if(item.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,item);return copy;
    }
    @Override public void removed(Player player){super.removed(player);box.stopOpen(player);}
}
