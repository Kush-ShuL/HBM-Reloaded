package com.hbm.world.structure.cabinet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original two rows of four ordinary Slots; inventory rejects automation, not manual Slot insertion. */
public final class FilingCabinetMenu extends AbstractContainerMenu {
 public final Container cabinet;public final BlockPos position;
 public FilingCabinetMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
 private FilingCabinetMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof FilingCabinetBlockEntity cabinet?cabinet:new SimpleContainer(8),pos);}
 public FilingCabinetMenu(int id,Inventory inventory,Container cabinet,BlockPos pos){super(FilingCabinetContent.MENU.get(),id);this.cabinet=cabinet;position=pos;checkContainerSize(cabinet,8);cabinet.startOpen(inventory.player);for(int i=0;i<2;i++)for(int j=0;j<4;j++)addSlot(new Slot(cabinet,j+i*4,53+j*18,18+i*36));for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inventory,j+i*9+9,8+j*18,88+i*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,146));}
 @Override public boolean stillValid(Player player){return cabinet.stillValid(player);}@Override public void removed(Player player){super.removed(player);cabinet.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=7){if(!moveItemStackTo(stack,8,slots.size(),true))return ItemStack.EMPTY;slot.onQuickCraft(stack,copy);}else if(!moveItemStackTo(stack,0,8,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
