package com.hbm.machine.fluidtank;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original six unrestricted GUI slots, player slots and source shift-click routing. */
public final class IndustrialFluidTankMenu extends AbstractContainerMenu {
    private final Container machine;
    private final BlockPos position;
    public IndustrialFluidTankMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private IndustrialFluidTankMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof IndustrialFluidTankBlockEntity barrel?barrel:new SimpleContainer(6),pos);}
    public IndustrialFluidTankMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(IndustrialFluidTankContent.MENU.get(),id);checkContainerSize(machine,6);this.machine=machine;this.position=pos;machine.startOpen(inventory.player);
        int[] x={8,8,35,35,125,125},y={17,53,17,53,17,53};for(int i=0;i<6;i++)addSlot(new Slot(machine,i,x[i],y[i]));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
    }
    public BlockPos position(){return position;}
    public void cycleMode(){if(machine instanceof IndustrialFluidTankBlockEntity barrel)barrel.cycleMode();}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=5){if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,5,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
