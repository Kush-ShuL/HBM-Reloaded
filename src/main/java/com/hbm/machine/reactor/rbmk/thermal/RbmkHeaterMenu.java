package com.hbm.machine.reactor.rbmk.thermal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact single bare selector slot; source accepts any manual item and has no automation slots. */
public final class RbmkHeaterMenu extends AbstractContainerMenu {
    public final BlockPos position;
    public RbmkHeaterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,new SimpleContainer(1),buffer.readBlockPos());}
    public RbmkHeaterMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(RbmkThermalContent.MENU.get(),id);position=pos;addSlot(new Slot(machine,0,41,45));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,104+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,162));}
    @Override public boolean stillValid(Player player){return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var source=slot.getItem();var copy=source.copy();if(index==0){if(!moveItemStackTo(source,1,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(source,0,1,false))return ItemStack.EMPTY;if(source.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
