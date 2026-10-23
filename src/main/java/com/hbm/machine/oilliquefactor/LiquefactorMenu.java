package com.hbm.machine.oilliquefactor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Literal source0..3slots; shift into0..2 (exclusive end3),not a new smart routing rule. */
public final class LiquefactorMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public LiquefactorMenu(int id,Inventory inv,RegistryFriendlyByteBuf b){this(id,inv,new SimpleContainer(4),b.readBlockPos());}
 public LiquefactorMenu(int id,Inventory inv,Container machine,BlockPos pos){super(LiquefactorContent.MENU.get(),id);this.position=pos;this.machine=machine;machine.startOpen(inv.player);addSlot(new Slot(machine,0,35,54));addSlot(new Slot(machine,1,134,72));addSlot(upgrade(machine,2,98,36));addSlot(upgrade(machine,3,98,54));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,9+y*9+x,8+x*18,122+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,180));}
 private static Slot upgrade(Container c,int i,int x,int y){return new Slot(c,i,x,y){@Override public boolean mayPlace(ItemStack stack){return com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack);}};}
 @Override public boolean stillValid(Player player){return machine instanceof LiquefactorEntity m?m.stillValid(player):position.distToCenterSqr(player.position())<=144;}@Override public void removed(Player p){super.removed(p);machine.stopOpen(p);}
 @Override public ItemStack quickMoveStack(Player p,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=3){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
