package com.hbm.world.structure.templates.microwave;
import com.hbm.recipe.generic.GenericOutputSlot;
import java.math.BigInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original3+36 slot geometry and reverse input/battery shift-transfer order; no invented onPickup call. */
public final class MicrowaveMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;private int fallbackTime,fallbackSpeed;
    public MicrowaveMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private MicrowaveMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof MicrowaveBlockEntity microwave?microwave:new SimpleContainer(3),pos);}
    public MicrowaveMenu(int id,Inventory inventory,MicrowaveBlockEntity machine){this(id,inventory,machine,machine.getBlockPos());}
    private MicrowaveMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(MicrowaveContent.MENU.get(),id);this.machine=machine;position=pos;addSlot(new Slot(machine,0,80,35));addSlot(new GenericOutputSlot(inventory.player,machine,1,140,35));addSlot(new Slot(machine,2,8,53));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+18*col,84+18*row));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+18*col,142));addDataSlot(new DataSlot(){@Override public int get(){return time();}@Override public void set(int value){if(machine instanceof MicrowaveBlockEntity microwave)microwave.time=value;fallbackTime=value;}});addDataSlot(new DataSlot(){@Override public int get(){return speed();}@Override public void set(int value){if(machine instanceof MicrowaveBlockEntity microwave)microwave.speed=value;fallbackSpeed=value;}});}
    public BigInteger power(){return machine instanceof MicrowaveBlockEntity microwave?microwave.getPowerExact():BigInteger.ZERO;}
    public int time(){return machine instanceof MicrowaveBlockEntity microwave?microwave.time:fallbackTime;}
    public int speed(){return machine instanceof MicrowaveBlockEntity microwave?microwave.speed:fallbackSpeed;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<=2){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,true)&&!moveItemStackTo(stack,2,3,true))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return original;}
}
