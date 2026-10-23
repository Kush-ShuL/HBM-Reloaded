package com.hbm.machine.generator.diesel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original4 slots plus36 player cells, including the original <=4 shift-click boundary. */
public final class DieselMenu extends AbstractContainerMenu {
    private final Container machine;public final BlockPos position;
    public DieselMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}private DieselMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof DieselBlockEntity engine?engine:new SimpleContainer(4),pos);}
    public DieselMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(DieselContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,17,17));addSlot(new Slot(machine,1,17,53){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,2,141,71));addSlot(new Slot(machine,3,35,71));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,121+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,179));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false)&&!moveItemStackTo(stack,2,3,false)&&!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
