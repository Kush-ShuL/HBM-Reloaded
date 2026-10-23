package com.hbm.machine.electricpress;

import com.hbm.item.StampItem;
import com.hbm.item.battery.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Literal source5+36slots,ordinary manual slots and original battery/upgrade/stamp/input shift priority. */
public final class ElectricPressMenu extends AbstractContainerMenu {
    private final Container machine;
    public final BlockPos position;
    public ElectricPressMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,new SimpleContainer(5),buffer.readBlockPos());}
    public ElectricPressMenu(int id,Inventory inventory,Container machine,BlockPos position){super(ElectricPressContent.MENU.get(),id);this.machine=machine;this.position=position;checkContainerSize(machine,5);addSlot(new Slot(machine,0,152,54));addSlot(new Slot(machine,1,19,15));addSlot(new Slot(machine,2,19,51));
        addSlot(new Slot(machine,3,79,33){private int craftBuffer;@Override public boolean mayPlace(ItemStack stack){return false;}@Override public ItemStack remove(int count){craftBuffer+=Math.min(count,getItem().getCount());return super.remove(count);}@Override protected void onQuickCraft(ItemStack stack,int count){craftBuffer+=count;crafted(stack);}private void crafted(ItemStack stack){stack.onCraftedBy(inventory.player.level(),inventory.player,craftBuffer);com.hbm.machine.press.PressCraftingHooks.crafted(inventory.player,stack);craftBuffer=0;}@Override public void onTake(Player player,ItemStack stack){crafted(stack);super.onTake(player,stack);}});
        addSlot(new Slot(machine,4,111,32){@Override public boolean mayPlace(ItemStack stack){return com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack);}});for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,9+y*9+x,8+x*18,104+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,162));}
    @Override public boolean stillValid(Player player){return machine instanceof ElectricPressEntity source?source.stillValid(player):position.distToCenterSqr(player.position())<=144;}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size()||!slots.get(index).hasItem())return ItemStack.EMPTY;var slot=slots.get(index);var stack=slot.getItem();var copy=stack.copy();if(index<=4){if(!moveItemStackTo(stack,5,slots.size(),true))return ItemStack.EMPTY;}else{int target=stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem?0:com.hbm.machine.oilmixer.MixerEntity.isUpgradeItem(stack)?4:stack.getItem() instanceof StampItem?1:2;if(!moveItemStackTo(stack,target,target+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
