package com.hbm.machine.press;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The original 13+36 slots and shift-click routing; manual slots intentionally accept any item. */
public final class PressMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;
    private final BlockPos pos;
    public PressMenu(int id, Inventory player, RegistryFriendlyByteBuf buffer) { this(id,player,new SimpleContainer(13),new SimpleContainerData(4),buffer.readBlockPos()); }
    public PressMenu(int id, Inventory player, Container machine, ContainerData data, BlockPos pos) {
        super(PressContent.MENU.get(),id); checkContainerSize(machine,13); checkContainerDataCount(data,4);
        this.machine=machine; this.data=data; this.pos=pos;
        addSlot(manual(machine,0,26,53)); addSlot(manual(machine,1,80,17)); addSlot(manual(machine,2,80,53));
        addSlot(new Slot(machine,3,140,35) {
            private int craftBuffer;
            @Override public boolean mayPlace(ItemStack stack) { return false; }
            @Override public ItemStack remove(int count) { craftBuffer+=Math.min(count,getItem().getCount()); return super.remove(count); }
            @Override protected void onQuickCraft(ItemStack stack,int count) { craftBuffer+=count; onCrafted(stack); }
            private void onCrafted(ItemStack stack) { stack.onCraftedBy(player.player.level(),player.player,craftBuffer); PressCraftingHooks.crafted(player.player,stack); craftBuffer=0; }
            @Override public void onTake(Player viewer,ItemStack stack) { onCrafted(stack); super.onTake(viewer,stack); }
        });
        for(int col=0;col<9;col++) addSlot(manual(machine,4+col,8+col*18,84));
        for(int row=0;row<3;row++) for(int col=0;col<9;col++) addSlot(new Slot(player,col+row*9+9,8+col*18,132+row*18));
        for(int col=0;col<9;col++) addSlot(new Slot(player,col,8+col*18,190));
        addDataSlots(data);
    }
    private static Slot manual(Container inventory,int slot,int x,int y) { return new Slot(inventory,slot,x,y) { @Override public boolean mayPlace(ItemStack stack) { return true; } }; }
    public BlockPos position() { return pos; }
    public int speed() { return data.get(0); }
    public int burnTime() { return (data.get(1)&65535)|(data.get(2)&65535)<<16; }
    public int press() { return (short)data.get(3); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0 || index>=slots.size()) return ItemStack.EMPTY;
        Slot slot=slots.get(index); if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack=slot.getItem(),copy=stack.copy();
        if(index<=12) { if(!moveItemStackTo(stack,13,slots.size(),true)) return ItemStack.EMPTY; }
        else {
            int target=PressBlockEntity.fuelTime(stack)>0 ? 0 : stack.getItem() instanceof com.hbm.item.StampItem ? 1 : 2;
            if(!moveItemStackTo(stack,target,target+1,false) && !moveItemStackTo(stack,4,13,false)) return ItemStack.EMPTY;
        }
        if(stack.getCount()==copy.getCount()) return ItemStack.EMPTY;
        if(stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        // X5846 transferStackInSlot does not dispatch output crafting callbacks here.
        return copy;
    }
}
