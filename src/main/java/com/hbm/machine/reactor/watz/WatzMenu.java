package com.hbm.machine.reactor.watz;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source24SlotNonRetarded positions in the exact clipped6×6 arrangement. */
public final class WatzMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public WatzMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
    private WatzMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof WatzEntity watz?watz:new SimpleContainer(24),pos);}
    public WatzMenu(int id,Inventory player,Container machine,BlockPos pos){super(WatzContent.MENU.get(),id);this.machine=machine;position=pos;checkContainerSize(machine,24);int index=0;for(int y=0;y<6;y++)for(int x=0;x<6;x++)if(x+y>1&&x+y<9&&5-x+y>1&&x+5-y>1){addSlot(new Slot(machine,index++,17+x*18,8+y*18){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(getContainerSlot(),stack);}@Override public int getMaxStackSize(){return 1;}});}for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(player,x+y*9+9,8+x*18,147+y*18));for(int x=0;x<9;x++)addSlot(new Slot(player,x,8+x*18,205));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<24){if(!moveItemStackTo(stack,24,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,24,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
