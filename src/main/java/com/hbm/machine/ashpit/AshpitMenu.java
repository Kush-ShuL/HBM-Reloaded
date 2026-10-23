package com.hbm.machine.ashpit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original five SlotTakeOnly coordinates, player rows and output-only shift transfer. */
public final class AshpitMenu extends AbstractContainerMenu {
 private final Container machine;public final BlockPos position;
 public AshpitMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}private AshpitMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof AshpitBlockEntity machine?machine:new SimpleContainer(5),pos);}
 public AshpitMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(AshpitContent.MENU.get(),id);this.machine=machine;this.position=pos;machine.startOpen(inventory.player);for(int i=0;i<5;i++)addSlot(new Slot(machine,i,44+i*18,27){@Override public boolean mayPlace(ItemStack stack){return false;}});for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,86+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,144));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}@Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>4)return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
