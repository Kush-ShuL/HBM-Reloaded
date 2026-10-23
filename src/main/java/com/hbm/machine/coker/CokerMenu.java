package com.hbm.machine.coker;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact2source slots plus36player,identifier-onlyshift routing and actual crafting output hooks. */
public final class CokerMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public CokerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}private CokerMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof CokerEntity tile?tile:new SimpleContainer(2),pos);}
 public CokerMenu(int id,Inventory player,Container machine,BlockPos pos){super(CokerContent.MENU.get(),id);this.position=pos;this.machine=machine;checkContainerSize(machine,2);machine.startOpen(player.player);addSlot(new Slot(machine,0,35,72));addSlot(new GenericOutputSlot(player.player,machine,1,97,27));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,180));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=1){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
