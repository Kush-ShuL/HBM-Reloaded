package com.hbm.machine.largeturbine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original SlotTakeOnly outputs are not crafting/XP slots; original unfiltered shift battery-first routing. */
public final class LargeTurbineMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public LargeTurbineMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,new SimpleContainer(7),buffer.readBlockPos());}
 public LargeTurbineMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(LargeTurbineContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,8,17));output(machine,1,8,53);addSlot(new Slot(machine,2,44,17));output(machine,3,44,53);addSlot(new Slot(machine,4,98,53));addSlot(new Slot(machine,5,152,17));output(machine,6,152,53);for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,84+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,142));}
 private void output(Container c,int slot,int x,int y){addSlot(new Slot(c,slot,x,y){@Override public boolean mayPlace(ItemStack stack){return false;}});}
 @Override public boolean stillValid(Player player){return machine instanceof LargeTurbineEntity t?t.stillValid(player):position.distToCenterSqr(player.position())<=144;}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=6){if(!moveItemStackTo(stack,7,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,4,5,false)&&!moveItemStackTo(stack,2,3,false)&&!moveItemStackTo(stack,5,6,false)&&!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
