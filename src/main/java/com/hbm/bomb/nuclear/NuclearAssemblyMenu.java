package com.hbm.bomb.nuclear;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original nine assembly slot layouts and withdraw-only shift transfer. */
public final class NuclearAssemblyMenu extends AbstractContainerMenu {
    private final NuclearAssembly assembly;
    public NuclearAssemblyMenu(MenuType<?> type,int id,Inventory inventory,NuclearAssembly assembly){
        super(type,id);this.assembly=assembly;var spec=assembly.definition();
        for(var position:spec.slots())addSlot(new Slot(assembly,position.index(),position.x(),position.y()));
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(inventory,column+row*9+9,spec.gui().playerX()+column*18,spec.gui().playerY()+row*18));
        for(int column=0;column<9;column++)addSlot(new Slot(inventory,column,spec.gui().playerX()+column*18,spec.gui().playerY()+58));
    }
    public NuclearAssembly assembly(){return assembly;}
    @Override public boolean stillValid(Player player){return assembly.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=assembly.getContainerSize())return ItemStack.EMPTY;Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack item=slot.getItem(),copy=item.copy();
        if(!moveItemStackTo(item,assembly.getContainerSize(),slots.size(),true))return ItemStack.EMPTY;
        if(item.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}
