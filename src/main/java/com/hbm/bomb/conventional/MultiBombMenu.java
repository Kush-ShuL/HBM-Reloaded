package com.hbm.bomb.conventional;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Source ContainerBombMulti: exact six positions and quick-move only from the bomb into player inventory. */
public final class MultiBombMenu extends AbstractContainerMenu {
    private final Container bomb;
    public MultiBombMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(6){@Override public int getMaxStackSize(){return 1;}});}
    public MultiBombMenu(int id,Inventory inventory,Container bomb){super(MultiBombContent.MENU.get(),id);checkContainerSize(bomb,6);this.bomb=bomb;for(int row=0;row<2;row++)for(int col=0;col<3;col++)addSlot(new Slot(bomb,row*3+col,44+col*18,26+row*18));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));}
    public int type(int slot){return MultiBombBlockEntity.type(bomb.getItem(slot));}
    @Override public boolean stillValid(Player player){return bomb.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem()||index>5)return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return before;}
}
