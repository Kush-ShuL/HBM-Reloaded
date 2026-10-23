package com.hbm.network.cargo;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class DroneCrateMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container crate;
 public DroneCrateMenu(int id,Inventory inv,RegistryFriendlyByteBuf buf){this(id,inv,buf.readBlockPos());}private DroneCrateMenu(int id,Inventory inv,BlockPos pos){this(id,inv,inv.player.level().getBlockEntity(pos) instanceof DroneCrateEntity c?c:new SimpleContainer(19),pos);}public DroneCrateMenu(int id,Inventory inv,DroneCrateEntity tile){this(id,inv,tile,tile.getBlockPos());}
 private DroneCrateMenu(int id,Inventory inv,Container tile,BlockPos pos){super(CargoDroneContent.CRATE_MENU.get(),id);position=pos;crate=tile;for(int y=0;y<3;y++)for(int x=0;x<6;x++)addSlot(new Slot(tile,x+y*6,8+x*18,17+y*18));addSlot(new Slot(tile,18,125,53));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inv,x+y*9+9,8+x*18,103+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inv,x,8+x*18,161));}
 @Override public boolean stillValid(Player player){return crate.stillValid(player);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=18){if(!moveItemStackTo(stack,19,slots.size(),true))return ItemStack.EMPTY;}else{if(copy.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,18,19,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,18,false))return ItemStack.EMPTY;return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
