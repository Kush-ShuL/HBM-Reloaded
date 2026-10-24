package com.hbm.machine.conveyorgrabber;

import com.hbm.machine.conveyorpackaging.CraneMetaUpgradeItem;
import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Nine source non-consuming SlotPatterns plus two genuine SlotUpgrade positions. */
public final class ConveyorGrabberMenu extends AbstractContainerMenu {
    public final ConveyorGrabberEntity grabber;
    public ConveyorGrabberMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,resolve(inventory,data.readBlockPos()));}
    private static ConveyorGrabberEntity resolve(Inventory inventory,BlockPos pos){return inventory.player.level().getBlockEntity(pos)instanceof ConveyorGrabberEntity grabber?grabber:new ConveyorGrabberEntity(pos,ConveyorGrabberContent.BLOCK.get().defaultBlockState());}
    public ConveyorGrabberMenu(int id,Inventory inventory,ConveyorGrabberEntity grabber){
        super(ConveyorGrabberContent.MENU.get(),id);this.grabber=grabber;
        for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(new Slot(grabber,col+row*3,40+col*18,17+row*18){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}});
        addSlot(upgrade(grabber,9,121,23));addSlot(upgrade(grabber,10,121,47));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,103+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,161));
    }
    private static Slot upgrade(ConveyorGrabberEntity machine,int index,int x,int y){return new Slot(machine,index,x,y){@Override public boolean mayPlace(ItemStack stack){return PrecisionUpgrades.isUpgrade(stack);}};}
    @Override public void clicked(int index,int button,ClickType type,Player player){
        if(index<0||index>8){super.clicked(index,button,type,player);return;}
        var slot=slots.get(index);
        if(button==1&&type==ClickType.PICKUP&&slot.hasItem()){if(!player.level().isClientSide)grabber.nextMode(index);}
        else{slot.set(getCarried());if(!player.level().isClientSide){grabber.matcher.initPatternStandard(slot.getItem(),index);grabber.setChanged();}}
        broadcastChanges();
    }
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<9||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var current=slot.getItem();var before=current.copy();
        if(index<11){if(!moveItemStackTo(current,11,slots.size(),true))return ItemStack.EMPTY;}
        else{if(CraneMetaUpgradeItem.isStack(before)){if(!moveItemStackTo(current,9,10,false))return ItemStack.EMPTY;}else if(CraneMetaUpgradeItem.isEjector(before)){if(!moveItemStackTo(current,10,11,false))return ItemStack.EMPTY;}return ItemStack.EMPTY;}
        if(current.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,current);return before;
    }
    @Override public boolean canDragTo(Slot slot){return slot.container!=grabber||slot.getContainerSlot()>=9;}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,Slot slot){return(slot.container!=grabber||slot.getContainerSlot()>=9)&&super.canTakeItemForPickAll(stack,slot);}
    @Override public boolean stillValid(Player player){return grabber.stillValid(player);}
}
