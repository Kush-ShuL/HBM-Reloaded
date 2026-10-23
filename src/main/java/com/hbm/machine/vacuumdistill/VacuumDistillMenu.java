package com.hbm.machine.vacuumdistill;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original12+36 lanes, hidden deprecated pressure-incompatible container slots, original shift routing. */
public final class VacuumDistillMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;private final BlockPos pos;
    public VacuumDistillMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private VacuumDistillMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof VacuumDistillBlockEntity be?be:new SimpleContainer(12),new SimpleContainerData(4),pos);}
    public VacuumDistillMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){
        super(VacuumDistillContent.MENU.get(),id);checkContainerSize(machine,12);checkContainerDataCount(data,4);this.machine=machine;this.data=data;this.pos=pos;machine.startOpen(player.player);
        addSlot(new Slot(machine,0,26,90));addSlot(deprecated(machine,1,44,90));addSlot(deprecated(machine,2,44,108));
        for(int i=0;i<4;i++){addSlot(new Slot(machine,3+i*2,80+i*18,90));addSlot(takeOnly(machine,4+i*2,80+i*18,108));}
        addSlot(new Slot(machine,11,26,108));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,156+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,214));addDataSlots(data);
    }
    private static Slot deprecated(Container c,int index,int x,int y){return new Slot(c,index,x,y){@Override public boolean mayPlace(ItemStack s){return false;}@Override public boolean isActive(){return false;}};}
    private static Slot takeOnly(Container c,int index,int x,int y){return new Slot(c,index,x,y){@Override public boolean mayPlace(ItemStack s){return false;}};}
    public BlockPos position(){return pos;}public long power(){long value=0;for(int i=0;i<4;i++)value|=(data.get(i)&65535L)<<(i*16);return value;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=10){if(!moveItemStackTo(stack,11,slots.size(),true))return ItemStack.EMPTY;}else{boolean moved=false;for(int target:new int[]{0,1,3,5,7,9})if(moveItemStackTo(stack,target,target+1,false)){moved=true;break;}if(!moved)return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
