package com.hbm.machine.annihilator;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original11 slots and176x208 placement. Output grid order is row-major2rows x3cols. */
public final class AnnihilatorMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public AnnihilatorMenu(int id,Inventory inv,RegistryFriendlyByteBuf data){this(id,inv,new SimpleContainer(11),data.readBlockPos());}
 public AnnihilatorMenu(int id,Inventory inv,Container machine,BlockPos pos){super(AnnihilatorContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new InputSlot(machine,0,17,45));addSlot(new InputSlot(machine,1,35,45));for(int y=0;y<2;y++)for(int x=0;x<3;x++)addSlot(new GenericOutputSlot(inv.player,machine,2+y*3+x,80+x*18,36+y*18));addSlot(new InputSlot(machine,8,152,18));addSlot(new InputSlot(machine,9,152,62));addSlot(new GenericOutputSlot(inv.player,machine,10,152,80));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,9+y*9+x,8+x*18,126+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,184));}
 private static final class InputSlot extends Slot{InputSlot(Container container,int index,int x,int y){super(container,index,x,y);}@Override public boolean mayPlace(ItemStack stack){return getContainerSlot()!=1||stack.getItem() instanceof FluidIdentifier;}@Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var current=slot.getItem();var original=current.copy();if(index<11){GenericOutputSlot.checkAchievements(player,current);if(!moveItemStackTo(current,11,slots.size(),true))return ItemStack.EMPTY;}else if(current.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(current,1,2,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(current,0,1,false))return ItemStack.EMPTY;if(current.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,current);return original;}
 @Override public boolean stillValid(Player player){return machine instanceof AnnihilatorEntity entity?entity.stillValid(player):position.distToCenterSqr(player.position())<=144;}
}
