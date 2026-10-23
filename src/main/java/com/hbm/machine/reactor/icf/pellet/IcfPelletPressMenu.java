package com.hbm.machine.reactor.icf.pellet;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
public final class IcfPelletPressMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;private final Level level;
 public IcfPelletPressMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
 private IcfPelletPressMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos)instanceof IcfPelletPressEntity press?press:new SimpleContainer(8),pos);}
 public IcfPelletPressMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(IcfPelletPressContent.MENU.get(),id);this.machine=machine;position=pos;level=inventory.player.level();int[][]coords={{98,18},{98,54},{8,18},{8,54},{62,54},{134,54},{62,18},{134,18}};for(int i=0;i<8;i++){final int index=i;addSlot(new Slot(machine,i,coords[i][0],coords[i][1]){@Override public boolean mayPlace(ItemStack stack){return index!=1&&index!=3;}});}for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,97+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,155));}
 @Override public boolean stillValid(Player player){return machine instanceof IcfPelletPressEntity?machine.stillValid(player):player.distanceToSqr(position.getX()+.5,position.getY()+.5,position.getZ()+.5)<=64;}
 @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var held=slot.getItem();var copy=held.copy();boolean moved;if(index<8)moved=moveItemStackTo(held,8,slots.size(),true);else if(held.is(HbmContent.item("icf_pellet_empty")))moved=moveItemStackTo(held,0,1,false);else if(held.getItem()instanceof FluidIdentifier)moved=moveItemStackTo(held,6,8,false);else if(held.is(HbmContent.item("particle_muon")))moved=moveItemStackTo(held,2,3,false);else moved=moveItemStackTo(held,4,6,false);if(!moved)return ItemStack.EMPTY;if(held.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
