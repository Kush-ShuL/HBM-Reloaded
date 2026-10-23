package com.hbm.machine.pyrooven;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original six slots and thirty-six player slots, including real SlotCraftingOutput semantics. */
public final class PyroOvenMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public PyroOvenMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,new SimpleContainer(6),data.readBlockPos());}
 public PyroOvenMenu(int id,Inventory player,Container machine,BlockPos pos){super(PyroOvenContent.MENU.get(),id);checkContainerSize(machine,6);this.machine=machine;position=pos;addSlot(new Slot(machine,0,152,72));addSlot(new Slot(machine,1,35,45));addSlot(new GenericOutputSlot(player.player,machine,2,89,45));addSlot(new Slot(machine,3,8,72));addSlot(new Slot(machine,4,71,72));addSlot(new Slot(machine,5,89,72));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,180));}
 @Override public boolean stillValid(Player player){return machine instanceof PyroOvenEntity entity?entity.stillValid(player):position.distToCenterSqr(player.position())<=64;}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=5){if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;}else{int start,end;if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){start=0;end=1;}else if(stack.getItem() instanceof FluidIdentifier){start=3;end=4;}else if(PyroOvenEntity.upgrade(stack)){start=4;end=6;}else{start=1;end=2;}if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
