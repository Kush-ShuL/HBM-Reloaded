package com.hbm.machine.forcefield;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.world.entity.player.*;import net.minecraft.world.inventory.*;import net.minecraft.world.item.ItemStack;
/** Exact original3plain slots and shift ordering, including unrestricted manual upgrade-slot placement. */
public final class ForceFieldMenu extends AbstractContainerMenu {
 public final ForceFieldBlockEntity field;
 public ForceFieldMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,(ForceFieldBlockEntity)inventory.player.level().getBlockEntity(buffer.readBlockPos()));}
 public ForceFieldMenu(int id,Inventory inventory,ForceFieldBlockEntity field){super(ForceFieldContent.MENU.get(),id);this.field=field;int[][] positions={{26,53},{89,35},{107,35}};for(int i=0;i<3;i++)addSlot(new Slot(field,i,positions[i][0],positions[i][1]){@Override public boolean mayPlace(ItemStack stack){return true;}});for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));}
 @Override public boolean stillValid(Player player){return field.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<3){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,1,3,false)&&!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return original;}
}
