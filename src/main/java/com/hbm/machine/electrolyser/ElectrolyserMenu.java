package com.hbm.machine.electrolyser;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** The two original menus expose distinct views of the same21-slot machine; shift-click rules stay separate. */
public final class ElectrolyserMenu extends AbstractContainerMenu {
 private final Container machine;private final ContainerData data;private final BlockPos pos;public final boolean metal;
 public ElectrolyserMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer,boolean metal){this(id,player,buffer.readBlockPos(),metal);}
 private ElectrolyserMenu(int id,Inventory player,BlockPos pos,boolean metal){this(id,player,player.player.level().getBlockEntity(pos) instanceof ElectrolyserBlockEntity be?be:new SimpleContainer(21),new SimpleContainerData(4),pos,metal);}
 public ElectrolyserMenu(int id,Inventory player,ElectrolyserBlockEntity machine,boolean metal){this(id,player,machine,new ContainerData(){public int get(int i){return(int)(machine.getPower()>>>(i*16))&65535;}public void set(int i,int v){}public int getCount(){return 4;}},machine.getBlockPos(),metal);}
 private ElectrolyserMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos,boolean metal){
  super(metal?ElectrolyserContent.METAL_MENU.get():ElectrolyserContent.FLUID_MENU.get(),id);checkContainerSize(machine,21);this.machine=machine;this.data=data;this.pos=pos;this.metal=metal;machine.startOpen(player.player);
  addSlot(new Slot(machine,0,186,109));addSlot(new Slot(machine,1,186,140));addSlot(new Slot(machine,2,186,158));
  if(metal){addSlot(new Slot(machine,14,10,22));int i=15;for(int row=0;row<3;row++)for(int col=0;col<2;col++)addSlot(takeOnly(machine,i++,136+col*18,18+row*18));}
  else{addSlot(new Slot(machine,3,6,18));addSlot(takeOnly(machine,4,6,54));addSlot(new Slot(machine,5,24,18));addSlot(takeOnly(machine,6,24,54));addSlot(new Slot(machine,7,78,18));addSlot(takeOnly(machine,8,78,54));addSlot(new Slot(machine,9,134,18));addSlot(takeOnly(machine,10,134,54));for(int i=11;i<=13;i++)addSlot(takeOnly(machine,i,154,18+(i-11)*18));}
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,180));addDataSlots(data);
 }
 private static Slot takeOnly(Container c,int i,int x,int y){return new Slot(c,i,x,y){@Override public boolean mayPlace(ItemStack s){return false;}};}
 public BlockPos position(){return pos;}public long power(){long n=0;for(int i=0;i<4;i++)n|=(data.get(i)&65535L)<<(i*16);return n;}
 public ElectrolyserBlockEntity machine(){return machine instanceof ElectrolyserBlockEntity be?be:null;}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();int start=metal?11:14;boolean moved;
  if(index<start)moved=moveItemStackTo(stack,start,slots.size(),true);
  else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem)moved=moveItemStackTo(stack,0,1,false);
  else if(stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem)moved=moveItemStackTo(stack,1,3,false);
  else if(metal)moved=moveItemStackTo(stack,3,4,false);
  else if(stack.getItem() instanceof FluidIdentifier)moved=moveItemStackTo(stack,3,4,false);else moved=false;
  if(!moved)return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
 }
}
