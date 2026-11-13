package com.hbm.machine.solidifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source output, battery, upgrades and fluid identifier with validated shift-click routing. */
public final class SolidifierMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public SolidifierMenu(int id,Inventory inv,RegistryFriendlyByteBuf b){this(id,inv,new SimpleContainer(5),b.readBlockPos());}
 public SolidifierMenu(int id,Inventory inv,Container machine,BlockPos pos){super(SolidifierContent.MENU.get(),id);this.position=pos;this.machine=machine;machine.startOpen(inv.player);addSlot(new com.hbm.recipe.generic.GenericOutputSlot(inv.player,machine,0,71,45));addSlot(input(machine,1,134,72));addSlot(input(machine,2,98,36));addSlot(input(machine,3,98,54));addSlot(input(machine,4,71,72));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,9+y*9+x,8+x*18,122+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,180));}
 private static Slot input(Container c,int i,int x,int y){return new Slot(c,i,x,y){@Override public boolean mayPlace(ItemStack stack){return c.canPlaceItem(i,stack);}};}
 @Override public boolean stillValid(Player player){return machine instanceof SolidifierEntity m?m.stillValid(player):position.distToCenterSqr(player.position())<=144;}@Override public void removed(Player p){super.removed(p);machine.stopOpen(p);}
 @Override public ItemStack quickMoveStack(Player p,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,5,false))return ItemStack.EMPTY;if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(index==0)slot.onQuickCraft(stack,copy);if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);return copy;}
}
