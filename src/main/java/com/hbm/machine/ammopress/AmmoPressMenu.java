package com.hbm.machine.ammopress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class AmmoPressMenu extends AbstractContainerMenu {
 public final Container machine;public final BlockPos position;
 public AmmoPressMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
 private AmmoPressMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof AmmoPressEntity press?press:new SimpleContainer(10),pos);}
 public AmmoPressMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(AmmoPressContent.MENU.get(),id);this.machine=machine;position=pos;for(int y=0;y<3;y++)for(int x=0;x<3;x++)addSlot(new Slot(machine,y*3+x,116+x*18,18+y*18));addSlot(new Slot(machine,9,134,72){@Override public boolean mayPlace(ItemStack stack){return false;}@Override public void onTake(Player player,ItemStack stack){stack.onCraftedBy(player.level(),player,stack.getCount());super.onTake(player,stack);}});for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,118+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,176));}
 @Override public boolean stillValid(Player player){return machine instanceof AmmoPressEntity?machine.stillValid(player):player.distanceToSqr(position.getX()+.5,position.getY()+.5,position.getZ()+.5)<=64;}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var held=slot.getItem();var copy=held.copy();if(index<10){if(!moveItemStackTo(held,10,slots.size(),true))return ItemStack.EMPTY;if(held.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}if(!(machine instanceof AmmoPressEntity press)||press.selected()==null)return ItemStack.EMPTY;var recipe=press.selected();for(int i=0;i<9;i++){var input=recipe.cell(i);if(input!=null&&input.matches(held,true)&&!moveItemStackTo(held,i,i+1,false))return ItemStack.EMPTY;}return ItemStack.EMPTY;}
}
