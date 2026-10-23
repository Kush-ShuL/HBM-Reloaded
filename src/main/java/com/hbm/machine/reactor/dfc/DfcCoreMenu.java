package com.hbm.machine.reactor.dfc;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class DfcCoreMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public DfcCoreMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private DfcCoreMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof DfcCoreEntity core?core:new SimpleContainer(3),pos);}
    public DfcCoreMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(DfcCoreContent.MENU.get(),id);this.position=pos;this.machine=machine;for(int i=0;i<3;i++)addSlot(new Slot(machine,i,62+i*18,53));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,84+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,142));}
    @Override public boolean stillValid(Player player){return machine instanceof DfcCoreEntity?machine.stillValid(player):player.distanceToSqr(position.getX()+.5,position.getY()+.5,position.getZ()+.5)<=144;}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(!(index<3?moveItemStackTo(stack,3,slots.size(),true):moveItemStackTo(stack,0,3,false)))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
