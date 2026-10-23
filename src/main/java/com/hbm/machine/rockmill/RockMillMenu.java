package com.hbm.machine.rockmill;
import com.hbm.item.battery.*;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source 8+36 slots at unchanged 176x220 GUI coordinates and original shift-click routing. */
public final class RockMillMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public RockMillMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
 private RockMillMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof RockMillEntity mill?mill:new SimpleContainer(8),pos);}
 public RockMillMenu(int id,Inventory player,Container machine,BlockPos pos){super(RockMillContent.MENU.get(),id);checkContainerSize(machine,8);this.machine=machine;position=pos;machine.startOpen(player.player);addSlot(restricted(machine,0,152,91));addSlot(restricted(machine,1,35,90));for(int i=0;i<3;i++)addSlot(restricted(machine,2+i,8+i*18,27));for(int i=0;i<3;i++)addSlot(new GenericOutputSlot(player.player,machine,5+i,80+i*18,27));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,138+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,196));}
 private static Slot restricted(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(index,stack);}@Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}};}
 public void selectRecipe(int index,String selection){if(machine instanceof RockMillEntity mill)mill.selectRecipe(index,selection);}
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<8){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,8,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(stack.is(Blueprints.ITEM.get())){if(!moveItemStackTo(stack,1,2,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,2,5,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
