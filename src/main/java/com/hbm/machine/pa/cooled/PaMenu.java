package com.hbm.machine.pa.cooled;
import com.hbm.item.battery.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source slot positions including original five-slot source/detector quick-move index boundary. */
public final class PaMenu extends AbstractContainerMenu {
 public final PaKind kind;public final BlockPos position;private final Container machine;
 public PaMenu(int id,Inventory inv,RegistryFriendlyByteBuf buffer){this(id,inv,buffer.readBlockPos());}
 private PaMenu(int id,Inventory inv,BlockPos pos){this(((PaBlock)inv.player.level().getBlockState(pos).getBlock()).kind,id,inv,inv.player.level().getBlockEntity(pos) instanceof PaCooledEntity be?be:new SimpleContainer(((PaBlock)inv.player.level().getBlockState(pos).getBlock()).kind.slots),pos);}
 public PaMenu(PaKind kind,int id,Inventory inv,Container machine,BlockPos pos){super(PaContent.MENU.get(),id);this.kind=kind;this.machine=machine;this.position=pos;checkContainerSize(machine,kind.slots);int bx=kind==PaKind.RFC?53:kind==PaKind.QUADRUPOLE?26:8;addSlot(new Slot(machine,0,bx,72){@Override public boolean mayPlace(ItemStack stack){return true;}});switch(kind){case RFC->{}case QUADRUPOLE->addSlot(new Slot(machine,1,71,36));case DIPOLE->addSlot(new Slot(machine,1,89,26));case SOURCE,DETECTOR->{boolean source=kind==PaKind.SOURCE;addSlot(new Slot(machine,1,62,source?16:18));addSlot(new Slot(machine,2,80,source?16:18));for(int i=0;i<2;i++)if(source)addSlot(new Slot(machine,3+i,62+i*18,43){@Override public boolean mayPlace(ItemStack stack){return false;}});else addSlot(new com.hbm.recipe.generic.GenericOutputSlot(inv.player,machine,3+i,62+i*18,45));}}for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,180));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}
 public void control(net.minecraft.nbt.CompoundTag tag){if(machine instanceof PaCooledEntity be)be.control(tag);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(kind==PaKind.SOURCE||kind==PaKind.DETECTOR){if(index<=5){if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,1,3,false))return ItemStack.EMPTY;}else if(index<kind.slots){if(!moveItemStackTo(stack,kind.slots,slots.size(),true))return ItemStack.EMPTY;}else if(kind==PaKind.RFC){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else {int start=stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem?0:1;if(!moveItemStackTo(stack,start,start+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
