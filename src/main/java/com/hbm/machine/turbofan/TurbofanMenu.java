package com.hbm.machine.turbofan;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class TurbofanMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;public int afterburner;
 public TurbofanMenu(int id,Inventory inv,RegistryFriendlyByteBuf buf){this(id,inv,new SimpleContainer(5),buf.readBlockPos());}
 public TurbofanMenu(int id,Inventory inv,Container machine,BlockPos pos){super(TurbofanContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,17,17));addSlot(new Slot(machine,1,17,53){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,2,98,71));addSlot(new Slot(machine,3,143,71));addSlot(new Slot(machine,4,44,71));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,x+y*9+9,8+x*18,121+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,179));addDataSlot(new DataSlot(){@Override public int get(){return machine instanceof TurbofanEntity t?t.afterburner:afterburner;}@Override public void set(int value){afterburner=value;}});}
 @Override public boolean stillValid(Player player){return machine instanceof TurbofanEntity t?t.stillValid(player):position.distToCenterSqr(player.position())<=144;}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else{int target=stack.getItem() instanceof HbmBattery?3:stack.getItem() instanceof FluidIdentifier?4:PrecisionUpgrades.isUpgrade(stack)?2:0;if(!moveItemStackTo(stack,target,target+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
