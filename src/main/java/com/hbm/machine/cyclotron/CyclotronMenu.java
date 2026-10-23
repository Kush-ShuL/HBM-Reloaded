package com.hbm.machine.cyclotron;
import com.hbm.item.battery.*;
import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source12slots and its actual <=15 shift boundary, not a corrected guessed 12-slot route. */
public final class CyclotronMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public CyclotronMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,new SimpleContainer(12),buffer.readBlockPos());}
 public CyclotronMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(CyclotronContent.MENU.get(),id);this.machine=machine;position=pos;
  for(int i=0;i<3;i++)addSlot(new Slot(machine,i,11,18+i*18));for(int i=0;i<3;i++)addSlot(new Slot(machine,i+3,101,18+i*18));for(int i=0;i<3;i++)addSlot(new GenericOutputSlot(inventory.player,machine,i+6,131,18+i*18));addSlot(new Slot(machine,9,168,83));
  for(int i=0;i<2;i++)addSlot(new Slot(machine,10+i,60+i*18,81){@Override public boolean mayPlace(ItemStack stack){return PrecisionUpgrades.isUpgrade(stack);}});
  for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,15+x*18,133+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,15+x*18,191));
 }
 @Override public boolean stillValid(Player player){return machine instanceof CyclotronEntity c?c.stillValid(player):position.distToCenterSqr(player.position())<=144;}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
  if(index<=15){if(!moveItemStackTo(stack,16,slots.size(),true))return ItemStack.EMPTY;}
  else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,9,10,true))return ItemStack.EMPTY;}
  else if(PrecisionUpgrades.isUpgrade(stack)){if(!moveItemStackTo(stack,10,11,true)&&!moveItemStackTo(stack,11,12,true))return ItemStack.EMPTY;}
  else if(CyclotronEntity.particle(stack)){if(!moveItemStackTo(stack,0,3,true))return ItemStack.EMPTY;}
  else if(!moveItemStackTo(stack,3,6,true))return ItemStack.EMPTY;
  if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
 }
}
