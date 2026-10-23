package com.hbm.machine.arcfurnace;
import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original30 inventory slots and source queue-directed quick move, not a vanilla furnace menu. */
public final class ArcFurnaceMenu extends AbstractContainerMenu {
 public final ArcFurnaceBlockEntity machine;
 public ArcFurnaceMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,(ArcFurnaceBlockEntity)inventory.player.level().getBlockEntity(data.readBlockPos()));}
 public ArcFurnaceMenu(int id,Inventory player,ArcFurnaceBlockEntity machine){super(ArcFurnaceContent.MENU.get(),id);this.machine=machine;
  for(int i=0;i<3;i++)addSlot(new SourceSlot(machine,i,62+i*18,22));addSlot(new Slot(machine,3,8,108));addSlot(new Slot(machine,4,152,108));
  for(int i=0;i<4;i++)for(int j=0;j<5;j++)addSlot(new SourceSlot(machine,5+j+i*5,44+j*18,54+i*18){@Override public boolean mayPlace(ItemStack stack){if(machine.liquidMode)return true;var recipe=machine.recipe(stack);return recipe!=null&&!recipe.solid().isEmpty()&&recipe.solid().getCount()*stack.getCount()<=recipe.solid().getMaxStackSize()&&stack.getCount()<=machine.getMaxInputSize();}@Override public int getMaxStackSize(){return hasItem()?machine.getMaxInputSize():1;}});
  for(int i=0;i<5;i++)addSlot(new SourceSlot(machine,25+i,44+i*18,129));
  for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(player,j+i*9+9,8+j*18,174+i*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,232));
 }
 @Override public boolean stillValid(Player player){return machine.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
  if(index<=29){if(!moveItemStackTo(stack,30,slots.size(),true))return ItemStack.EMPTY;}
  else{int start,end;if(stack.getItem() instanceof HbmBattery){start=3;end=4;}else if(stack.is(ArcFurnaceContent.ELECTRODE.get())){start=0;end=3;}else if(stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem){start=4;end=5;}else{start=25;end=30;}if(!sourceMerge(stack,start,end))return ItemStack.EMPTY;}
  if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return original;
 }
 private boolean sourceMerge(ItemStack stack,int start,int end){boolean changed=false;if(stack.isStackable())for(int i=start;i<end&&!stack.isEmpty();i++){var slot=slots.get(i);var current=slot.getItem();int max=Math.min(stack.getMaxStackSize(),slot.getMaxStackSize());if(!current.isEmpty()&&ItemStack.isSameItemSameComponents(stack,current)&&current.getCount()<max&&slot.mayPlace(stack.copyWithCount(Math.min(stack.getCount(),max)))){int move=Math.min(stack.getCount(),max-current.getCount());current.grow(move);stack.shrink(move);slot.setChanged();changed=true;}}for(int i=start;i<end&&!stack.isEmpty();i++){var slot=slots.get(i);int amount=Math.min(stack.getCount(),Math.min(slot.getMaxStackSize(),stack.getMaxStackSize()));if(!slot.hasItem()&&slot.mayPlace(stack.copyWithCount(amount))){slot.set(stack.split(amount));changed=true;}}return changed;}
 private static class SourceSlot extends Slot {SourceSlot(Container container,int slot,int x,int y){super(container,slot,x,y);}@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(getContainerSlot(),stack);}@Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}}
}
