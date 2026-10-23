package com.hbm.machine.mininglaser;

import com.hbm.machine.oilmixer.MixerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact ContainerMiningLaser slots, including plain battery/output Slot accepting manual placement and output->player only shift rule. */
public final class MiningLaserMenu extends AbstractContainerMenu {
    private final Container machine;
    public final BlockPos position;
    public MiningLaserMenu(int id,Inventory inv,RegistryFriendlyByteBuf buf){this(id,inv,buf.readBlockPos());}
    private MiningLaserMenu(int id,Inventory inv,BlockPos pos){this(id,inv,inv.player.level().getBlockEntity(pos) instanceof MiningLaserEntity be?be:new SimpleContainer(30),pos);}
    public MiningLaserMenu(int id,Inventory inv,Container machine,BlockPos pos){
        super(MiningLaserContent.MENU.get(),id);this.machine=machine;position=pos;
        addSlot(new Slot(machine,0,8,108));
        for(int i=0;i<2;i++)for(int j=0;j<4;j++)addSlot(new Slot(machine,1+i*4+j,98+j*18,18+i*18){@Override public boolean mayPlace(ItemStack stack){return MixerEntity.isUpgradeItem(stack);}});
        for(int i=0;i<3;i++)for(int j=0;j<7;j++)addSlot(new Slot(machine,9+i*7+j,44+j*18,72+i*18));
        for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inv,j+i*9+9,8+j*18,140+i*18));
        for(int j=0;j<9;j++)addSlot(new Slot(inv,j,8+j*18,198));
    }
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<30){if(!moveItemStackTo(stack,30,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,9,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();return original;}
}
