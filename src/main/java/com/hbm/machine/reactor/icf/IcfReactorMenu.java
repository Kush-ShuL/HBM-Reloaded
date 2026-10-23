package com.hbm.machine.reactor.icf;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class IcfReactorMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public IcfReactorMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
    private IcfReactorMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof IcfReactorEntity reactor?reactor:new SimpleContainer(12),pos);}
    public IcfReactorMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(IcfReactorContent.MENU.get(),id);this.position=pos;this.machine=machine;for(int i=0;i<5;i++)addSlot(new Slot(machine,i,80+i*18,18));addSlot(new Slot(machine,5,116,54));for(int i=0;i<5;i++)addSlot(new GenericOutputSlot(inventory.player,machine,6+i,80+i*18,90));addSlot(new Slot(machine,11,44,90));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,44+x*18,140+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,44+x*18,198));}
    @Override public boolean stillValid(Player player){return machine instanceof IcfReactorEntity?machine.stillValid(player):player.distanceToSqr(position.getX()+.5,position.getY()+.5,position.getZ()+.5)<=256;}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var held=slot.getItem();var copy=held.copy();boolean moved;if(index<=11)moved=moveItemStackTo(held,12,slots.size(),true);else if(held.getItem()instanceof FluidIdentifier)moved=moveItemStackTo(held,11,12,false);else{moved=moveItemStackTo(held,5,6,false);if(!moved)moved=moveItemStackTo(held,0,5,false);}if(!moved)return ItemStack.EMPTY;if(held.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
