package com.hbm.machine.compressor;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class CompressorMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;private final ContainerData data;
 public CompressorMenu(int id,Inventory player,RegistryFriendlyByteBuf buf){this(id,player,buf.readBlockPos());}
 private CompressorMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof CompressorBlockEntity be?be:new SimpleContainer(4),new SimpleContainerData(4),pos);}
 public CompressorMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){super(CompressorContent.MENU.get(),id);this.position=pos;this.machine=machine;this.data=data;checkContainerSize(machine,4);checkContainerDataCount(data,4);machine.startOpen(player.player);addSlot(new Slot(machine,0,17,72));addSlot(new Slot(machine,1,152,72));addSlot(new Slot(machine,2,52,72));addSlot(new Slot(machine,3,70,72));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,180));addDataSlots(data);}
 public long power(){long value=0;for(int i=0;i<4;i++)value|=(data.get(i)&65535L)<<(i*16);return value;}
 public void control(int pressure){if(machine instanceof CompressorBlockEntity be)be.compression(pressure);}
 @Override public boolean stillValid(Player p){return machine.stillValid(p);}@Override public void removed(Player p){super.removed(p);machine.stopOpen(p);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<4){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}else{int start=stack.getItem() instanceof HbmBattery?1:stack.getItem() instanceof FluidIdentifier?0:2,end=start<2?start+1:4;if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
