package com.hbm.machine.plasmaforge;
import com.hbm.item.battery.*;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact original16+36 slots; ordinary manual slots are intentionally unrestricted as in SlotNonRetarded. */
public final class PlasmaForgeMenu extends AbstractContainerMenu {
    public final PlasmaForgeBlockEntity machine;
    public PlasmaForgeMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,(PlasmaForgeBlockEntity)player.player.level().getBlockEntity(buffer.readBlockPos()));}
    public PlasmaForgeMenu(int id,Inventory player,PlasmaForgeBlockEntity machine){super(PlasmaForgeContent.MENU.get(),id);this.machine=machine;machine.startOpen(player.player);addSlot(new Slot(machine,0,152,82));addSlot(new Slot(machine,1,35,81));addSlot(new Slot(machine,2,98,116));for(int i=0;i<12;i++)addSlot(new Slot(machine,i+3,8+i%4*18,18+i/4*18));addSlot(new GenericOutputSlot(player.player,machine,15,116,36));for(int i=0;i<27;i++)addSlot(new Slot(player,9+i,8+i%9*18,162+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,220));}
    public BlockPos position(){return machine.getBlockPos();}
    public void selectRecipe(int index,String selection){machine.selectRecipe(index,selection);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<16){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,16,slots.size(),true))return ItemStack.EMPTY;}else{int from=stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem?0:stack.getItem()==Blueprints.ITEM.get()?1:2;int to=from==2?15:from+1;if(!insertOriginal(stack,from,to))return ItemStack.EMPTY;}if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
    /** Original InventoryUtil.mergeItemStack additionally checks isItemValidForSlot for shift inserts. */
    private boolean insertOriginal(ItemStack stack,int from,int to){boolean moved=false;for(int pass=0;pass<2;pass++)for(int i=from;i<to&&!stack.isEmpty();i++){if(i>=2&&!machine.canPlaceItem(i,stack))continue;var current=machine.getItem(i);if(pass==0&&!current.isEmpty()&&ItemStack.isSameItemSameComponents(current,stack)){int amount=Math.min(stack.getCount(),Math.max(0,Math.min(64,current.getMaxStackSize())-current.getCount()));if(amount>0){current.grow(amount);stack.shrink(amount);machine.setChanged();moved=true;}}else if(pass==1&&current.isEmpty()){int amount=Math.min(stack.getCount(),Math.min(64,stack.getMaxStackSize()));machine.setItem(i,stack.copyWithCount(amount));stack.shrink(amount);moved=true;}}return moved;}
}
