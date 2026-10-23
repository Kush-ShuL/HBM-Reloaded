package com.hbm.machine.reactor.iter.torus;
import com.hbm.item.battery.*;
import com.hbm.recipe.generic.Blueprints;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact original three slots/player coordinates and SlotNonRetarded validity. */
public final class TorusMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public TorusMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
 private TorusMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof TorusEntity entity?entity:new SimpleContainer(3),pos);}
 public TorusMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(TorusContent.MENU.get(),id);this.machine=machine;position=pos;checkContainerSize(machine,3);int[][] coords={{8,82},{71,81},{130,36}};for(int i=0;i<3;i++){int slot=i;addSlot(new Slot(machine,i,coords[i][0],coords[i][1]){@Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(slot,stack);}});}for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,35+x*18,162+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,35+x*18,220));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=2){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(stack.is(Blueprints.ITEM.get())){if(!moveItemStackTo(stack,1,2,false))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
