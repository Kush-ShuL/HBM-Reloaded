package com.hbm.machine.autocrafter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source57 slots, nonconsuming1-item pattern copies, preview cycle and battery-only player quick transfer. */
public final class AutocrafterMenu extends AbstractContainerMenu {
    public final AutocrafterBlockEntity machine;
    public AutocrafterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,resolve(inventory,buffer.readBlockPos()));}
    private static AutocrafterBlockEntity resolve(Inventory inventory,BlockPos pos){return inventory.player.level().getBlockEntity(pos) instanceof AutocrafterBlockEntity machine?machine:new AutocrafterBlockEntity(pos,AutocrafterContent.BLOCK.get().defaultBlockState());}
    public AutocrafterMenu(int id,Inventory inventory,AutocrafterBlockEntity machine){super(AutocrafterContent.MENU.get(),id);this.machine=machine;
        for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(pattern(row*3+col,44+col*18,22+row*18,false));addSlot(pattern(9,116,40,true));
        for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(new Slot(machine,10+row*3+col,44+col*18,86+row*18));
        addSlot(new Slot(machine,19,116,104));addSlot(new Slot(machine,20,17,99));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,158+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,216));
    }
    private Slot pattern(int index,int x,int y,boolean allowCount){return new Slot(machine,index,x,y){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return allowCount?64:1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:allowCount?stack.copy():stack.copyWithCount(1));}};}
    @Override public void clicked(int index,int button,ClickType type,Player player){if(index<0||index>9){super.clicked(index,button,type,player);return;}var slot=getSlot(index);if(index==9){if(button==1&&type==ClickType.PICKUP&&slot.hasItem()){machine.nextTemplate();broadcastChanges();}return;}if(button==1&&type==ClickType.PICKUP&&slot.hasItem())machine.nextMode(index);else machine.setTemplateFromMenu(index,getCarried());}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index>=10&&index<=20){if(!moveItemStackTo(stack,21,slots.size(),true))return ItemStack.EMPTY;}else if(index>20){if(!AutocrafterBlockEntity.battery(stack)||!moveItemStackTo(stack,20,21,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return original;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
}
