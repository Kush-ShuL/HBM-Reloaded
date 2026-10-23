package com.hbm.world.structure.templates.funnel;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original54 slots and source transferStackInSlot, without an invented shift-click onPickup call. */
public final class FunnelMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;private int fallbackMode;
    public FunnelMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private FunnelMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof FunnelBlockEntity funnel?funnel:new SimpleContainer(18),pos);}
    public FunnelMenu(int id,Inventory inventory,FunnelBlockEntity machine){this(id,inventory,machine,machine.getBlockPos());}
    private FunnelMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(FunnelContent.MENU.get(),id);this.machine=machine;position=pos;for(int i=0;i<9;i++)addSlot(new Slot(machine,i,8+18*i,18));for(int i=0;i<9;i++)addSlot(new GenericOutputSlot(inventory.player,machine,i+9,8+18*i,54));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,86+row*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,144));addDataSlot(new DataSlot(){@Override public int get(){return mode();}@Override public void set(int value){if(machine instanceof FunnelBlockEntity funnel)funnel.mode=value;fallbackMode=value;}});}
    public int mode(){return machine instanceof FunnelBlockEntity funnel?funnel.mode:fallbackMode;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=17){if(!moveItemStackTo(stack,18,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,9,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
