package com.hbm.machine.centrifuge;

import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact source8+36 slots; manual input and battery accept arbitrary stacks, automation remains restricted. */
public final class CentrifugeMenu extends AbstractContainerMenu {
    private final Container machine; private final ContainerData data; private final BlockPos pos;
    public CentrifugeMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer) { this(id,inventory,new SimpleContainer(8),new SimpleContainerData(14),buffer.readBlockPos()); }
    public CentrifugeMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos) {
        super(CentrifugeContent.MENU.get(),id); checkContainerSize(machine,8); checkContainerDataCount(data,14); this.machine=machine; this.data=data; this.pos=pos;
        addSlot(manual(machine,0,44,57)); addSlot(manual(machine,1,8,57));
        for(int i=0;i<4;i++) addSlot(new Slot(machine,i+2,70+i*20,57) {
            private int crafted;
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public ItemStack remove(int count) { crafted+=Math.min(count,getItem().getCount()); return super.remove(count); }
            private void onCrafted(Player viewer,ItemStack stack) { stack.onCraftedBy(viewer.level(),viewer,crafted); CentrifugeCraftingHooks.crafted(viewer,stack); crafted=0; }
            @Override protected void onQuickCraft(ItemStack stack,int count) { crafted+=count; onCrafted(player.player,stack); }
            @Override public void onTake(Player viewer,ItemStack stack) { onCrafted(viewer,stack); super.onTake(viewer,stack); }
        });
        for(int i=0;i<2;i++) addSlot(new Slot(machine,6+i,156,31+i*18) { @Override public boolean mayPlace(ItemStack stack) { return upgrade(stack); } });
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) addSlot(new Slot(player,9+row*9+col,11+col*18,107+row*18));
        for(int col=0;col<9;col++) addSlot(new Slot(player,col,11+col*18,165));
        addDataSlots(data);
    }
    private static Slot manual(Container machine,int id,int x,int y) { return new Slot(machine,id,x,y) { @Override public boolean mayPlace(ItemStack stack) { return true; } }; }
    public static boolean upgrade(ItemStack stack) { return stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem; }
    public BlockPos position() { return pos; }
    public long power() { long value=0; for(int i=0;i<4;i++) value|=(data.get(i)&65535L)<<(i*16); return value; }
    private int integer(int index) { return data.get(index)&65535|(data.get(index+1)&65535)<<16; }
    public int capacity() { return integer(4); }
    public int progress() { return integer(6); }
    public int processTime() { return integer(8); }
    public boolean progressing() { return data.get(10)!=0; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size()) return ItemStack.EMPTY; Slot slot=slots.get(index); if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack=slot.getItem(),copy=stack.copy();
        if(index<=7) { if(!moveItemStackTo(stack,8,slots.size(),true)) return ItemStack.EMPTY; slot.onQuickCraft(stack,copy); }
        else { int start=stack.getItem() instanceof HbmBattery?1:upgrade(stack)?6:0,end=start==6?8:start+1; if(!moveItemStackTo(stack,start,end,false)) return ItemStack.EMPTY; }
        if(stack.getCount()==copy.getCount()) return ItemStack.EMPTY;
        if(stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        // Source onSlotChange dispatches output crafting above; it does not additionally call onPickupFromSlot.
        return copy;
    }
}
