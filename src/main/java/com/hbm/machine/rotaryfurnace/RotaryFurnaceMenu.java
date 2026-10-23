package com.hbm.machine.rotaryfurnace;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
/** Original5+36 ordinary slots, fuel-first shift routing, no invented output or upgrade slots. */
public final class RotaryFurnaceMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public RotaryFurnaceMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}private RotaryFurnaceMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof RotaryFurnaceEntity furnace?furnace:new SimpleContainer(5),pos);}
 public RotaryFurnaceMenu(int id,Inventory player,Container machine,BlockPos pos){super(RotaryFurnaceContent.MENU.get(),id);checkContainerSize(machine,5);this.machine=machine;position=pos;machine.startOpen(player.player);for(int i=0;i<3;i++)addSlot(new Slot(machine,i,8+i*18,18));addSlot(new Slot(machine,3,8,54));addSlot(new Slot(machine,4,44,54));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,104+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,162));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}@Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getBurnTime(RecipeType.SMELTING)>0){if(!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,3,4,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
