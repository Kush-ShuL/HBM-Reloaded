package com.hbm.machine.strandcaster;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact source 7 machine slots, 176x214 GUI placement and original quick-move range. */
public final class StrandCasterMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container caster;
 public StrandCasterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,new SimpleContainer(7),data.readBlockPos());}
 public StrandCasterMenu(int id,Inventory inventory,Container caster,BlockPos pos){super(StrandCasterContent.MENU.get(),id);this.caster=caster;position=pos;addSlot(new Slot(caster,0,57,62){@Override public boolean mayPlace(ItemStack stack){return stack.getItem() instanceof com.hbm.machine.anvil.MoldItem;}@Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}});for(int i=0;i<3;i++)for(int j=0;j<2;j++)addSlot(new com.hbm.recipe.generic.GenericOutputSlot(inventory.player,caster,j+i*2+1,125+j*18,26+i*18));for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inventory,j+i*9+9,8+j*18,132+i*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,190));}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var original=slot.getItem();var copy=original.copy();if(index<=6){if(!moveItemStackTo(original,7,slots.size(),true))return ItemStack.EMPTY;slot.onQuickCraft(original,copy);}else if(!moveItemStackTo(original,1,2,false))return ItemStack.EMPTY;if(original.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
 @Override public boolean stillValid(Player player){return caster instanceof StrandCasterEntity machine?machine.stillValid(player):position.distToCenterSqr(player.position())<=128;}
}
