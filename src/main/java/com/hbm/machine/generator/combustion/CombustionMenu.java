package com.hbm.machine.generator.combustion;

import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Same five GUI slots and shift-click routing. Only source SlotCraftingOutput refuses manual placement. */
public final class CombustionMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public CombustionMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
    private CombustionMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof CombustionBlockEntity engine?engine:new SimpleContainer(5),pos);}
    public CombustionMenu(int id,Inventory player,Container machine,BlockPos pos){super(CombustionContent.MENU.get(),id);this.machine=machine;position=pos;checkContainerSize(machine,5);machine.startOpen(player.player);addSlot(new Slot(machine,0,17,17));addSlot(new Slot(machine,1,17,53){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,2,88,71));addSlot(new Slot(machine,3,143,71));addSlot(new Slot(machine,4,35,71));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(player,x+y*9+9,8+x*18,121+y*18));for(int x=0;x<9;x++)addSlot(new Slot(player,x,8+x*18,179));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else{int target=stack.getItem() instanceof HbmBattery?3:stack.getItem() instanceof FluidIdentifier?4:stack.getItem() instanceof PistonSetItem?2:0;if(!moveItemStackTo(stack,target,target+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
