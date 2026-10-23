package com.hbm.machine.conveyorpackaging;

import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original21 cargo slots,23 with two source SlotUpgrade positions; Boxer's source shift-click return quirk retained. */
public final class ConveyorPackagingMenu extends AbstractContainerMenu {
    public final Container machine;public final boolean unboxer;public final BlockPos position;private final int size;
    public ConveyorPackagingMenu(int id,Inventory inventory,boolean unboxer,RegistryFriendlyByteBuf data){this(id,inventory,unboxer,data.readBlockPos());}
    private ConveyorPackagingMenu(int id,Inventory inventory,boolean unboxer,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof ConveyorPackagingEntity crane&&crane.unboxer==unboxer?crane:new SimpleContainer(unboxer?23:21),unboxer,pos);}
    public ConveyorPackagingMenu(int id,Inventory inventory,Container machine,boolean unboxer,BlockPos pos){super((unboxer?ConveyorPackagingContent.UNBOXER_MENU:ConveyorPackagingContent.BOXER_MENU).get(),id);this.machine=machine;this.unboxer=unboxer;position=pos;size=unboxer?23:21;checkContainerSize(machine,size);machine.startOpen(inventory.player);for(int row=0;row<3;row++)for(int col=0;col<7;col++)addSlot(new Slot(machine,col+row*7,8+col*18,17+row*18));if(unboxer){addSlot(upgrade(machine,21,152,23));addSlot(upgrade(machine,22,152,47));}for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,103+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,161));}
    private static Slot upgrade(Container machine,int slot,int x,int y){return new Slot(machine,slot,x,y){@Override public boolean mayPlace(ItemStack stack){return PrecisionUpgrades.isUpgrade(stack);}};}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var current=slot.getItem();var before=current.copy();if(index<size){if(!moveItemStackTo(current,size,slots.size(),true))return ItemStack.EMPTY;}else{if(!moveItemStackTo(current,0,size,false))return ItemStack.EMPTY;if(!unboxer)return ItemStack.EMPTY;}if(current.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,current);return before;}
}
