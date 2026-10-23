package com.hbm.machine.generator.gasturbine;

import com.hbm.fluid.FluidTraits;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class GasTurbineMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;private final net.minecraft.world.level.Level level;
    public GasTurbineMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
    private GasTurbineMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof GasTurbineBlockEntity engine?engine:new SimpleContainer(2),pos);}
    public GasTurbineMenu(int id,Inventory player,Container machine,BlockPos pos){super(GasTurbineContent.MENU.get(),id);this.machine=machine;level=player.player.level();position=pos;checkContainerSize(machine,2);addSlot(new Slot(machine,0,8,109));addSlot(new Slot(machine,1,36,17));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(player,x+y*9+9,8+x*18,141+y*18));for(int x=0;x<9;x++)addSlot(new Slot(player,x,8+x*18,199));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=1){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery){if(!moveItemStackTo(stack,0,1,true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier identifier){var type=identifier.getType(level,position,stack);var trait=type.getTrait(FluidTraits.Combustible.class);if(trait==null||trait.getGrade()!=FluidTraits.FuelGrade.GAS||!moveItemStackTo(stack,1,2,true))return ItemStack.EMPTY;}else return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
