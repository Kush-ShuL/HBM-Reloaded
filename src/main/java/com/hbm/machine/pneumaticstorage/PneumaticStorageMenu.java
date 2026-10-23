package com.hbm.machine.pneumaticstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original real-slot and ghost-slot layouts; virtual patterns never enter player inventory. */
public final class PneumaticStorageMenu extends AbstractContainerMenu {
    public final PneumaticInventory machine;
    public PneumaticStorageMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,(PneumaticInventory)inventory.player.level().getBlockEntity(data.readBlockPos()));}
    public PneumaticStorageMenu(int id,Inventory inventory,PneumaticInventory machine){super(PneumaticStorageContent.MENU.get(),id);this.machine=machine;
        switch(machine.kind){
            case CLUTTER->{for(int i=0;i<54;i++)addSlot(new Slot(machine,i,8+i%9*18,17+i/9*18));}
            case MONO->{for(int i=0;i<3;i++)addSlot(ghost(i,8,17+i*18));}
            case IMPORTER->{for(int i=0;i<9;i++)addSlot(new Slot(machine,i,62+i%3*18,17+i/3*18));}
            case EXPORTER->{for(int i=0;i<9;i++)addSlot(ghost(i,17+i%3*18,17+i/3*18));for(int i=0;i<9;i++)addSlot(new Slot(machine,i+9,80+i%3*18,17+i/3*18){@Override public boolean mayPlace(ItemStack stack){return false;}});}
            default->throw new IllegalArgumentException("Access uses its own source menu");
        }
        int y=switch(machine.kind){case CLUTTER->153;case MONO->99;default->103;};for(int i=0;i<27;i++)addSlot(new Slot(inventory,9+i,8+i%9*18,y+i/9*18));for(int i=0;i<9;i++)addSlot(new Slot(inventory,i,8+i*18,y+58));
    }
    private Slot ghost(int index,int x,int y){return new Slot(machine,index,x,y){@Override public boolean mayPlace(ItemStack stack){return false;}@Override public boolean mayPickup(Player player){return false;}};}
    public BlockPos position(){return machine.getBlockPos();}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void clicked(int index,int button,ClickType type,Player player){int ghosts=machine.kind==PneumaticKind.MONO?3:machine.kind==PneumaticKind.EXPORTER?9:0;if(index>=0&&index<ghosts){if(machine instanceof PneumaticMono mono)mono.setPattern(index,getCarried());else machine.setItem(index,getCarried().isEmpty()?ItemStack.EMPTY:getCarried().copy());broadcastChanges();return;}super.clicked(index,button,type,player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(machine.kind==PneumaticKind.MONO||index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;int size=machine.getContainerSize();if(machine.kind==PneumaticKind.EXPORTER&&(index<9||index>=size))return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<size){if(!moveItemStackTo(stack,size,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,size,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
