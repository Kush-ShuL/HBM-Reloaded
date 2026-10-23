package com.hbm.machine.tapedrive;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original2x6 drive slots and36 player slots, including SlotNonRetarded's loaded-stack limit. */
public final class TapeDriveMenu extends AbstractContainerMenu {
    private final Container machine;
    private final BlockPos position;
    private record ClientInit(Container machine,BlockPos position){}
    public TapeDriveMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,read(inventory,buffer));}
    private TapeDriveMenu(int id,Inventory inventory,ClientInit init){this(id,inventory,init.machine(),init.position());}
    private static ClientInit read(Inventory inventory,RegistryFriendlyByteBuf buffer){BlockPos pos=buffer.readBlockPos();Container machine=inventory.player.level().getBlockEntity(pos) instanceof TapeDriveEntity drive?drive:new SimpleContainer(12);return new ClientInit(machine,pos);}
    public TapeDriveMenu(int id,Inventory inventory,Container machine,BlockPos pos){
        super(TapeDriveContent.MENU.get(),id);this.machine=machine;this.position=pos;
        for(int row=0;row<2;row++)for(int col=0;col<6;col++)addSlot(new Slot(machine,col+row*6,35+col*18,27+row*18){
            @Override public boolean mayPlace(ItemStack stack){return TapeDriveEntity.isDrive(stack);}
            @Override public int getMaxStackSize(){return Math.max(1,getItem().isEmpty()?1:getItem().getCount());}
        });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,104+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,162));
    }
    public BlockPos position(){return position;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        ItemStack stack=slot.getItem(),original=stack.copy();
        if(index<12){if(!moveItemStackTo(stack,12,slots.size(),true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,12,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();
        if(stack.getCount()==original.getCount())return ItemStack.EMPTY;
        slot.onTake(player,stack);return original;
    }
}
