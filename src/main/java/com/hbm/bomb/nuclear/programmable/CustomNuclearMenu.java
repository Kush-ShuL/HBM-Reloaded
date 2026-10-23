package com.hbm.bomb.nuclear.programmable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source 27-slot payload grid and bidirectional reverse-order shift transfer. */
public final class CustomNuclearMenu extends AbstractContainerMenu {
 public final Container bomb;
 public CustomNuclearMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,(CustomNuclearBlockEntity)inventory.player.level().getBlockEntity(buffer.readBlockPos()));}
 public CustomNuclearMenu(int id,Inventory inventory,Container bomb){super(ProgrammableNuclearContent.CUSTOM_MENU.get(),id);this.bomb=bomb;checkContainerSize(bomb,27);for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(bomb,col+row*9,8+col*18,18+row*18));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,140+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,198));}
 public CustomBombPayload payload(){return bomb instanceof CustomNuclearBlockEntity block?block.payload:CustomNuclearIngredients.calculate(bomb);}
 @Override public boolean stillValid(Player player){return bomb.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(!moveItemStackTo(stack,index<=26?27:0,index<=26?slots.size():27,true))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return before;}
}
