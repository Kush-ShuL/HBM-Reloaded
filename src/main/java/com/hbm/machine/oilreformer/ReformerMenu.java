package com.hbm.machine.oilreformer;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact11source slots,3take-only output lanes and source battery/selector/catalyst/canister shift routing. */
public final class ReformerMenu extends AbstractContainerMenu {
 private final Container machine;private final ContainerData data;private final BlockPos pos;
 public ReformerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}private ReformerMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ReformerEntity machine?machine:new SimpleContainer(11),new SimpleContainerData(4),pos);}
 public ReformerMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){super(ReformerContent.MENU.get(),id);checkContainerSize(machine,11);checkContainerDataCount(data,4);this.machine=machine;this.data=data;this.pos=pos;machine.startOpen(player.player);addSlot(new Slot(machine,0,17,90));addSlot(new Slot(machine,1,35,90));addSlot(takeOnly(machine,2,35,108));for(int i=0;i<3;i++){addSlot(new Slot(machine,3+i*2,107+i*18,90));addSlot(takeOnly(machine,4+i*2,107+i*18,108));}addSlot(new Slot(machine,9,17,108));addSlot(new Slot(machine,10,71,36));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,156+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,214));addDataSlots(data);}
 private static Slot takeOnly(Container c,int index,int x,int y){return new Slot(c,index,x,y){@Override public boolean mayPlace(ItemStack s){return false;}};}
 public BlockPos position(){return pos;}public long power(){long value=0;for(int i=0;i<4;i++)value|=(data.get(i)&65535L)<<(i*16);return value;}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=10){if(!moveItemStackTo(stack,11,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,9,10,false))return ItemStack.EMPTY;}else if(stack.is(HbmContent.item("catalytic_converter"))){if(!moveItemStackTo(stack,10,11,false))return ItemStack.EMPTY;}else{boolean moved=false;for(int target:new int[]{1,3,5,7})if(moveItemStackTo(stack,target,target+1,false)){moved=true;break;}if(!moved)return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
