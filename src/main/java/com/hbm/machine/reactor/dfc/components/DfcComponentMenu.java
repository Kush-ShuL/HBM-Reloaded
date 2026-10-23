package com.hbm.machine.reactor.dfc.components;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source exact machine slots, player positions and asymmetric shift routing; sided automation remains empty. */
public final class DfcComponentMenu extends AbstractContainerMenu {
 public final DfcComponentKind kind;public final BlockPos position;private final Container machine;
 public DfcComponentMenu(DfcComponentKind kind,int id,Inventory inventory,BlockPos pos){this(kind,id,inventory,inventory.player.level().getBlockEntity(pos) instanceof DfcComponentEntity component?component:new SimpleContainer(kind.slots),pos);}
 public DfcComponentMenu(DfcComponentKind kind,int id,Inventory inventory,Container machine,BlockPos pos){super(DfcComponentsContent.menu(kind),id);this.kind=kind;position=pos;this.machine=machine;machine.startOpen(inventory.player);if(kind==DfcComponentKind.INJECTOR){addSlot(new Slot(machine,0,26,17));addSlot(new GenericOutputSlot(inventory.player,machine,1,26,53));addSlot(new Slot(machine,2,134,17));addSlot(new GenericOutputSlot(inventory.player,machine,3,134,53));}if(kind==DfcComponentKind.STABILIZER)addSlot(new Slot(machine,0,80,17));for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,8+i%9*18,84+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,142));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size()||kind.slots==0)return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<kind.slots){if(!moveItemStackTo(stack,kind.slots,slots.size(),true))return ItemStack.EMPTY;}else{if(kind!=DfcComponentKind.STABILIZER||!moveItemStackTo(stack,0,1,true))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
