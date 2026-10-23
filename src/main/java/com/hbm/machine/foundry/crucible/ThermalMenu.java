package com.hbm.machine.foundry.crucible;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact original firebox2/crucible9 visible slots and source player coordinates; crucible hotbar swap is disabled. */
public final class ThermalMenu extends AbstractContainerMenu {
 private final Container machine;private final BlockPos pos;public final boolean firebox;
 public ThermalMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer,boolean firebox){this(id,inventory,buffer.readBlockPos(),firebox);}
 private ThermalMenu(int id,Inventory inventory,BlockPos pos,boolean firebox){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof ThermalMachine machine?machine:new SimpleContainer(firebox?2:10),pos,firebox);}
 public ThermalMenu(int id,Inventory inventory,Container machine,BlockPos pos,boolean firebox){super(firebox?CrucibleContent.FIREBOX_MENU.get():CrucibleContent.CRUCIBLE_MENU.get(),id);this.machine=machine;this.pos=pos;this.firebox=firebox;machine.startOpen(inventory.player);
  if(firebox){addSlot(new Slot(machine,0,44,27));addSlot(new Slot(machine,1,62,27));}else for(int row=0;row<3;row++)for(int col=0;col<3;col++){int slot=1+row*3+col;addSlot(new Slot(machine,slot,107+col*18,18+row*18){@Override public int getMaxStackSize(){return 1;}@Override public int getMaxStackSize(ItemStack stack){return 1;}});}
  int base=firebox?86:132,bar=firebox?144:190;for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,base+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,bar));
 }
 public BlockPos position(){return pos;}public void selectRecipe(String selection){if(machine instanceof CrucibleBlockEntity crucible)crucible.selectRecipe(selection);}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public void clicked(int slot,int button,ClickType type,Player player){if(!firebox&&type==ClickType.SWAP)return;super.clicked(slot,button,type,player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();int count=firebox?2:9;if(index<count){if(!moveItemStackTo(stack,count,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,count,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
