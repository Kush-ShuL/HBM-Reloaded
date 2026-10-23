package com.hbm.machine.launchpadsoyuz;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source ContainerBase27-slot topology, including unrestricted manual slots and shift insert into all27 slots. */
public final class LaunchpadSoyuzMenu extends AbstractContainerMenu {
    public final Container machine;private final BlockPos pos;
    public LaunchpadSoyuzMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private LaunchpadSoyuzMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof LaunchpadSoyuzEntity machine?machine:new SimpleContainer(27),pos);}
    public LaunchpadSoyuzMenu(int id,Inventory inventory,Container machine,BlockPos pos){
        super(LaunchpadSoyuzContent.MENU.get(),id);checkContainerSize(machine,27);this.machine=machine;this.pos=pos;
        int[][] coordinates={{98,80},{80,80},{98,26},{80,26},{152,98},{152,116},{170,98},{170,116},{134,98}};
        for(int i=0;i<9;i++)addSlot(new Slot(machine,i,coordinates[i][0],coordinates[i][1]));
        for(int i=0;i<3;i++)for(int j=0;j<6;j++)addSlot(new Slot(machine,j+i*6+9,44-i*18,26+j*18));
        for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inventory,j+i*9+9,17+j*18,162+i*18));
        for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,17+i*18,220));
    }
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();
        if(index<27){if(!moveItemStackTo(stack,27,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,27,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;
    }
}
