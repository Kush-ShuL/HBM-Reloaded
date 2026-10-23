package com.hbm.machine.conveyorextraction;

import com.hbm.conveyor.crane.CraneBaseEntity;
import com.hbm.machine.conveyorpackaging.ConveyorPackagingContent;
import com.hbm.machine.precisionassembler.PrecisionUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class CraneExtractionMenu extends AbstractContainerMenu {
    public final CraneBaseEntity crane;public final boolean extractor;private final int size;
    public CraneExtractionMenu(int id,Inventory inventory,boolean extractor,RegistryFriendlyByteBuf data){this(id,inventory,resolve(inventory,extractor,data.readBlockPos()),extractor);}
    private static CraneBaseEntity resolve(Inventory inventory,boolean extractor,BlockPos pos){var tile=inventory.player.level().getBlockEntity(pos);if(extractor&&tile instanceof CraneExtractorEntity crane)return crane;if(!extractor&&tile instanceof CraneInserterEntity crane)return crane;return extractor?new CraneExtractorEntity(pos,CraneExtractionContent.EXTRACTOR.get().defaultBlockState()):new CraneInserterEntity(pos,CraneExtractionContent.INSERTER.get().defaultBlockState());}
    public CraneExtractionMenu(int id,Inventory inventory,CraneBaseEntity crane,boolean extractor){
        super((extractor?CraneExtractionContent.EXTRACTOR_MENU:CraneExtractionContent.INSERTER_MENU).get(),id);this.crane=crane;this.extractor=extractor;size=extractor?20:21;
        if(extractor){
            for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(new Slot(crane,col+row*3,71+col*18,17+row*18){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}});
            for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(new Slot(crane,9+col+row*3,8+col*18,17+row*18){@Override public boolean mayPlace(ItemStack stack){return crane.canPlaceItem(getContainerSlot(),stack);}});
            addSlot(upgrade(crane,18,152,23));addSlot(upgrade(crane,19,152,47));
        }else for(int row=0;row<3;row++)for(int col=0;col<7;col++)addSlot(new Slot(crane,col+row*7,8+col*18,17+row*18));
        int px=extractor?26:8;for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,px+col*18,103+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,px+col*18,161));
    }
    private static Slot upgrade(CraneBaseEntity crane,int slot,int x,int y){return new Slot(crane,slot,x,y){@Override public boolean mayPlace(ItemStack stack){return PrecisionUpgrades.isUpgrade(stack);}};}
    @Override public void clicked(int index,int button,ClickType type,Player player){if(!extractor||index<0||index>8){super.clicked(index,button,type,player);return;}var slot=slots.get(index);var tile=(CraneExtractorEntity)crane;if(button==1&&type==ClickType.PICKUP&&slot.hasItem())tile.nextMode(index);else{slot.set(getCarried());tile.matcher.initPatternStandard(slot.getItem(),index);}broadcastChanges();}
    @Override public boolean canDragTo(Slot slot){return (!extractor||slot.container!=crane||slot.getContainerSlot()>8)&&super.canDragTo(slot);}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,Slot slot){return (!extractor||slot.container!=crane||slot.getContainerSlot()>8)&&super.canTakeItemForPickAll(stack,slot);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size()||extractor&&index<9)return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var current=slot.getItem();var before=current.copy();
        if(index<size){if(!moveItemStackTo(current,size,slots.size(),true))return ItemStack.EMPTY;}
        else if(extractor){int start=current.is(ConveyorPackagingContent.STACK.get())?18:current.is(ConveyorPackagingContent.EJECTOR.get())?19:9;int end=start==18?19:start==19?20:size;if(!moveItemStackTo(current,start,end,false))return ItemStack.EMPTY;return ItemStack.EMPTY;}
        else if(!moveItemStackTo(current,0,size,false))return ItemStack.EMPTY;
        if(current.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,current);return before;
    }
    @Override public boolean stillValid(Player player){return crane.stillValid(player);}
}
