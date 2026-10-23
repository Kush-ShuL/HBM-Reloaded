package com.hbm.machine.rtgblast;

import com.hbm.item.rtg.RtgPelletItem;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Real45 slots and source empty-input right-click side selection, using native container packets. */
public final class RtgBlastMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;
    public RtgBlastMenu(int id,Inventory inventory){this(id,inventory,new SimpleContainer(9),new SimpleContainerData(4));}
    public RtgBlastMenu(int id,Inventory inventory,Container machine,ContainerData data){
        super(RtgBlastContent.MENU.get(),id);checkContainerSize(machine,9);checkContainerDataCount(data,4);this.machine=machine;this.data=data;
        addSlot(new Slot(machine,0,80,18));addSlot(new Slot(machine,1,80,54));addSlot(new GenericOutputSlot(inventory.player,machine,2,134,36));
        for(int i=0;i<6;i++)addSlot(new Slot(machine,3+i,22+(i%2)*18,18+(i/2)*18){@Override public boolean mayPlace(ItemStack stack){return true;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));addDataSlots(data);
    }
    public int progress(){return(short)data.get(0);}
    public int heat(){return(short)data.get(1);}
    public int side(int slot){return(byte)data.get(slot==0?2:3);}
    public int scaledProgress(int size){return progress()*size/RtgBlastEntity.TIME_REQUIRED;}
    @Override public void clicked(int index,int button,ClickType type,Player player){
        if(index>=0&&index<2&&button==1&&type==ClickType.PICKUP&&!getSlot(index).hasItem()&&getCarried().isEmpty()){
            if(!player.level().isClientSide&&machine instanceof RtgBlastEntity furnace)furnace.cycleSide(index);return;
        }super.clicked(index,button,type,player);
    }
    @Override public ItemStack quickMoveStack(Player player,int index){
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
        if(index<=8){if(!moveItemStackTo(stack,9,slots.size(),true))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof RtgPelletItem){if(!moveItemStackTo(stack,3,9,false))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;
    }
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
}
