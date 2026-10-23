package com.hbm.machine.turret;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source ContainerTurretBase: exactly eleven unrestricted manual slots and its original chip-first shift routing. */
public class TurretMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public TurretMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof SentryEntity sentry?sentry:new SimpleContainer(11),pos);}
    public TurretMenu(int id,Inventory inventory,Container machine,BlockPos pos){this(TurretContent.MENU.get(),id,inventory,machine,pos);}
    protected TurretMenu(MenuType<?> type,int id,Inventory inventory,Container machine,BlockPos pos){super(type,id);this.machine=machine;position=pos;machine.startOpen(inventory.player);addSlot(new Slot(machine,0,98,27));for(int i=0;i<9;i++)addSlot(new Slot(machine,1+i,80+i%3*18,63+i/3*18));addSlot(new Slot(machine,10,152,99));for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,8+i%9*18,140+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,198));}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<11){if(!moveItemStackTo(stack,11,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof TurretChipItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,1,11,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;slot.onTake(player,stack);return copy;}
}
