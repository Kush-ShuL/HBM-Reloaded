package com.hbm.machine.oilmixer;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original five unrestricted manual slots and literal shift-click upgrade end4 (exclusive). */
public final class MixerMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public MixerMenu(int id,Inventory inv,RegistryFriendlyByteBuf data){this(id,inv,new SimpleContainer(5),data.readBlockPos());}
 public MixerMenu(int id,Inventory inv,Container machine,BlockPos pos){super(MixerContent.MENU.get(),id);this.position=pos;this.machine=machine;addSlot(new Slot(machine,0,12,72));addSlot(new Slot(machine,1,52,72));addSlot(new Slot(machine,2,126,72));addSlot(new Slot(machine,3,148,18));addSlot(new Slot(machine,4,148,36));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,9+y*9+x,8+x*18,122+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,180));}
 public void cycle(){if(machine instanceof MixerEntity mixer)mixer.cycle();}
 @Override public boolean stillValid(Player player){return machine instanceof MixerEntity mixer?mixer.stillValid(player):position.distToCenterSqr(player.position())<=144;}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else{int start,end;if(stack.getItem() instanceof HbmBattery){start=0;end=1;}else if(stack.getItem() instanceof FluidIdentifier){start=2;end=3;}else if(MixerEntity.isUpgradeItem(stack)){start=3;end=4;}else{start=1;end=2;}if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
