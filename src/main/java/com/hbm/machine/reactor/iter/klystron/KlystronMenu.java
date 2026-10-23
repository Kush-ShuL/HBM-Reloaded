package com.hbm.machine.reactor.iter.klystron;
import com.hbm.item.battery.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact source1slot+player/hotbar coordinates and battery-onlyshift insertion. */
public final class KlystronMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public KlystronMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
    private KlystronMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof KlystronEntity entity?entity:new SimpleContainer(1),pos);}
    public KlystronMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(KlystronContent.MENU.get(),id);this.machine=machine;position=pos;checkContainerSize(machine,1);addSlot(new Slot(machine,0,8,72));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,17+x*18,118+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,17+x*18,176));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index==0){if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}else if(!(stack.getItem() instanceof HbmBattery)&&!(stack.getItem() instanceof CreativeBatteryItem)||!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
