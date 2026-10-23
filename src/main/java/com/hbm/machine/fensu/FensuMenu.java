package com.hbm.machine.fensu;

import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact ContainerBatteryREDD two slots/player inventory; bigint display uses the same actual client BE snapshot. */
public final class FensuMenu extends AbstractContainerMenu {
    private final Container machine;private final net.minecraft.world.level.Level viewLevel;public final BlockPos pos;
    public FensuMenu(int id,Inventory inventory,RegistryFriendlyByteBuf extra){this(id,inventory,extra.readBlockPos());}
    private FensuMenu(int id,Inventory inventory,BlockPos position){this(id,inventory,inventory.player.level().getBlockEntity(position) instanceof FensuBlockEntity machine?machine:new SimpleContainer(2),position);}
    public FensuMenu(int id,Inventory inventory,Container machine,BlockPos position){super(FensuContent.MENU.get(),id);this.machine=machine;viewLevel=inventory.player.level();this.pos=position;checkContainerSize(machine,2);addSlot(new BatterySlot(machine,0,26,53));addSlot(new BatterySlot(machine,1,80,53));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,99+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,157));}
    /** Source SlotNonRetarded: both validation and the truthful oversized-stack display limit. */
    private static final class BatterySlot extends Slot{
        BatterySlot(Container container,int index,int x,int y){super(container,index,x,y);}
        @Override public boolean mayPlace(ItemStack stack){return stack.getItem() instanceof com.hbm.item.battery.HbmBattery&&container.canPlaceItem(getContainerSlot(),stack);}
        @Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}
    }
    private FensuBlockEntity view(){return viewLevel.getBlockEntity(pos) instanceof FensuBlockEntity fensu?fensu:null;}
    public BigInteger power(){var fensu=view();return fensu==null?BigInteger.ZERO:fensu.storedPower();}public BigInteger delta(){var fensu=view();return fensu==null?BigInteger.ZERO:fensu.delta;}
    public int redLow(){var fensu=view();return fensu==null?0:fensu.redLow;}public int redHigh(){var fensu=view();return fensu==null?2:fensu.redHigh;}public int priority(){var fensu=view();return fensu==null?1:fensu.getPriority().ordinal();}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int button){return machine instanceof FensuBlockEntity fensu&&fensu.stillValid(player)&&fensu.cycle(button);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=1){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,2,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
