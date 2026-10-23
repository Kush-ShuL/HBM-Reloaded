package com.hbm.machine.reactor.rbmk.autoloader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class RbmkAutoloaderMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public RbmkAutoloaderMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,new SimpleContainer(18),data.readBlockPos());}
    public RbmkAutoloaderMenu(int id,Inventory player,Container machine,BlockPos pos){super(RbmkAutoloaderContent.MENU.get(),id);this.machine=machine;position=pos;for(int y=0;y<3;y++)for(int x=0;x<3;x++)addSlot(new Slot(machine,x+y*3,17+x*18,18+y*18){@Override public boolean mayPlace(ItemStack stack){return player.player.level().getBlockEntity(position) instanceof RbmkAutoloaderEntity loader&&loader.canPlaceItem(getContainerSlot(),stack);}@Override public int getMaxStackSize(){return Math.max(64,getItem().getCount());}});for(int y=0;y<3;y++)for(int x=0;x<3;x++)addSlot(new Slot(machine,9+x+y*3,107+x*18,18+y*18){@Override public boolean mayPlace(ItemStack stack){return false;}});for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(player,9+x+y*9,8+x*18,100+y*18));for(int x=0;x<9;x++)addSlot(new Slot(player,x,8+x*18,158));}
    @Override public boolean stillValid(Player player){return machine instanceof RbmkAutoloaderEntity?machine.stillValid(player):true;}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(index<18){if(!moveItemStackTo(stack,18,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,9,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return before;}
}
