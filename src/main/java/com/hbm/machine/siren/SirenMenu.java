package com.hbm.machine.siren;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original1+36 slots and exact coordinates; manual slot remains unrestricted like1.7 Slot. */
public final class SirenMenu extends AbstractContainerMenu {
    public final SirenBlockEntity machine;
    public SirenMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,resolve(inventory,buffer.readBlockPos()));}
    private static SirenBlockEntity resolve(Inventory inventory,BlockPos pos){return inventory.player.level().getBlockEntity(pos) instanceof SirenBlockEntity machine?machine:new SirenBlockEntity(pos,SirenContent.BLOCK.get().defaultBlockState());}
    public SirenMenu(int id,Inventory inventory,SirenBlockEntity machine){super(SirenContent.MENU.get(),id);this.machine=machine;addSlot(new Slot(machine,0,8,35){@Override public boolean mayPlace(ItemStack stack){return true;}});for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=0){if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return original;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
}
