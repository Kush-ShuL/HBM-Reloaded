package com.hbm.machine.oilburner;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original container in0/out1/identifier2 and source identifier-aware shift transfer. */
public final class OilburnerMenu extends AbstractContainerMenu {
 private final Container machine;public final BlockPos position;
 public OilburnerMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}private OilburnerMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof OilburnerBlockEntity heater?heater:new SimpleContainer(3),pos);}
 public OilburnerMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(OilburnerContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,26,17));addSlot(new Slot(machine,1,26,53){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,2,44,71));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,121+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,179));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<3){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,2,3,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
 public void toggle(){if(machine instanceof OilburnerBlockEntity heater)heater.toggle();}
}
