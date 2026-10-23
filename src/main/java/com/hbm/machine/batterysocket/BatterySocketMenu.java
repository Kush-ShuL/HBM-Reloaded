package com.hbm.machine.batterysocket;

import com.hbm.item.battery.HbmBattery;
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

/** One validated battery slot at35,35; original inventory99/157 and GUI15-bit-safe long state. */
public final class BatterySocketMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;
    private final BlockPos position;
    public BatterySocketMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer) { this(id,inventory,new SimpleContainer(1),new SimpleContainerData(15),buffer.readBlockPos()); }
    public BatterySocketMenu(int id,Inventory inventory,Container machine,ContainerData data,BlockPos pos) {
        super(BatterySocketContent.MENU.get(),id);checkContainerSize(machine,1);checkContainerDataCount(data,15);this.machine=machine;this.data=data;this.position=pos;
        addSlot(new Slot(machine,0,35,35) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof HbmBattery&&machine.canPlaceItem(0,stack); }
            @Override public int getMaxStackSize() { return Math.max(machine.getMaxStackSize(),hasItem()?getItem().getCount():1); }
        });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,99+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,157));addDataSlots(data);
    }
    private long readLong(int start) {long value=0;for(int i=0;i<4;i++)value|=(data.get(start+i)&65535L)<<(i*16);return value;}
    public BlockPos position() {return position;}
    public long rawPower() {return readLong(0);}
    public long capacity() {return readLong(4);}
    public long delta() {return readLong(8);}
    public int redLow() {return data.get(12);}
    public int redHigh() {return data.get(13);}
    public int priority() {return data.get(14);}
    @Override public boolean stillValid(Player player) {return machine.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int button) {return machine instanceof BatterySocketBlockEntity socket&&socket.stillValid(player)&&socket.cycleButton(button);}
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0||index>=slots.size())return ItemStack.EMPTY;Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack stack=slot.getItem(),copy=stack.copy();
        if(index==0) {if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;
        if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}
