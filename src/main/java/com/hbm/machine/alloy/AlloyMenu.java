package com.hbm.machine.alloy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original four slots,36-player slots and empty-slot right-click side cycling. */
public final class AlloyMenu extends AbstractContainerMenu {
    private final Container machine; private final ContainerData data; private final BlockPos pos;
    public AlloyMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer) { this(id,player,new SimpleContainer(4),new SimpleContainerData(6),buffer.readBlockPos()); }
    public AlloyMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos) {
        super(AlloyContent.MENU.get(),id); this.machine=machine; this.data=data; this.pos=pos; checkContainerSize(machine,4); checkContainerDataCount(data,6);
        addSlot(new Slot(machine,0,80,18)); addSlot(new Slot(machine,1,80,54)); addSlot(new Slot(machine,2,8,36));
        addSlot(new Slot(machine,3,134,36) {
            private int crafted;
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public ItemStack remove(int count) { crafted+=Math.min(count,getItem().getCount()); return super.remove(count); }
            @Override public void onTake(Player viewer,ItemStack stack) { stack.onCraftedBy(viewer.level(),viewer,crafted); AlloyCraftingHooks.crafted(viewer,stack); crafted=0; super.onTake(viewer,stack); }
        });
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) addSlot(new Slot(player,9+row*9+col,8+col*18,84+row*18));
        for(int col=0;col<9;col++) addSlot(new Slot(player,col,8+col*18,142)); addDataSlots(data);
    }
    public int fuel() { return data.get(0); } public int progress() { return data.get(1); } public boolean canProcess() { return data.get(5)!=0; }
    public int side(int slot) { return data.get(slot==0?3:slot==1?4:2); } public BlockPos position() { return pos; }
    @Override public void clicked(int slot,int button,ClickType type,Player player) {
        if(slot>=0&&slot<3&&button==1&&type==ClickType.PICKUP&&!getSlot(slot).hasItem()&&getCarried().isEmpty()) { if(!player.level().isClientSide&&machine instanceof AlloyBlockEntity furnace) furnace.cycleSide(slot); return; }
        super.clicked(slot,button,type,player);
    }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size()||!getSlot(index).hasItem()) return ItemStack.EMPTY;
        Slot slot=getSlot(index); ItemStack stack=slot.getItem(),copy=stack.copy();
        if(index<=3) { if(!moveItemStackTo(stack,4,slots.size(),true)) return ItemStack.EMPTY; }
        else if(!moveItemStackTo(stack,0,3,false)) return ItemStack.EMPTY;
        if(stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        // ContainerDiFurnace intentionally has no onSlotChange/onTake callback for quick move.
        return copy;
    }
}
