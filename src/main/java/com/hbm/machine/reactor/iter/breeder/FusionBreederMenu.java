package com.hbm.machine.reactor.iter.breeder;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source3machine+36player slots; real SlotNonRetarded validators/output hooks and original shift routing. */
public final class FusionBreederMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public FusionBreederMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}private FusionBreederMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof FusionBreederEntity breeder?breeder:new SimpleContainer(3),pos);}
 public FusionBreederMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(FusionBreederContent.MENU.get(),id);position=pos;this.machine=machine;machine.startOpen(inventory.player);int[][] coords={{26,72},{48,45}};for(int i=0;i<2;i++){int slot=i;addSlot(new Slot(machine,i,coords[i][0],coords[i][1]){@Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(slot,stack);}@Override public int getMaxStackSize(){return Math.max(machine.getMaxStackSize(),hasItem()?getItem().getCount():1);}});}addSlot(new GenericOutputSlot(inventory.player,machine,2,112,45));for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,8+i%9*18,118+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,176));}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=2){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else{int first=stack.getItem() instanceof FluidIdentifier?0:1;if(!moveItemStackTo(stack,first,first+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
