package com.hbm.machine.arcwelder;

import com.google.gson.JsonParser;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidIdentifier;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact11machine+36player slot positions and original shift routing. */
public final class ArcWelderMenu extends AbstractContainerMenu {
    public final Container machine;private final BlockPos pos;
    /** Reuse original ItemMachineUpgrade identity set; no second upgrade registry. */
    public static boolean upgrade(ItemStack stack){return com.hbm.machine.soldering.SolderingMenu.upgrade(stack);}
    public ArcWelderMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private ArcWelderMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ArcWelderBlockEntity machine?machine:new SimpleContainer(8),pos);}
    public ArcWelderMenu(int id,Inventory player,Container machine,BlockPos pos){
        super(ArcWelderContent.MENU.get(),id);checkContainerSize(machine,8);this.machine=machine;this.pos=pos;machine.startOpen(player.player);
        for(int i=0;i<3;i++)addSlot(new Slot(machine,i,17+i*18,36));addSlot(new com.hbm.recipe.generic.GenericOutputSlot(player.player,machine,3,107,36));addSlot(new Slot(machine,4,152,72));addSlot(new Slot(machine,5,17,63));
        for(int i=0;i<2;i++)addSlot(new Slot(machine,6+i,89+i*18,63){@Override public boolean mayPlace(ItemStack stack){return upgrade(stack);}});
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+column*18,122+row*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,180));
    }
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size()||!getSlot(index).hasItem())return ItemStack.EMPTY;var slot=getSlot(index);var stack=slot.getItem();var copy=stack.copy();
        if(index<=7){if(!moveItemStackTo(stack,8,slots.size(),true))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,5,6,false))return ItemStack.EMPTY;}
        else if(upgrade(stack)){if(!moveItemStackTo(stack,6,8,false))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}
