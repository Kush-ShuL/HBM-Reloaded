package com.hbm.machine.oilflare;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original six slots, unrestricted manual Slot and take-only empty-container output. */
public final class FlareMenu extends AbstractContainerMenu {
    public final BlockPos position;private final Container machine;
    public FlareMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,new SimpleContainer(6),data.readBlockPos());}
    public FlareMenu(int id,Inventory inventory,Container machine,BlockPos position){super(FlareContent.MENU.get(),id);this.machine=machine;this.position=position;addSlot(new Slot(machine,0,143,71));addSlot(new Slot(machine,1,17,17));addSlot(new Slot(machine,2,17,53){@Override public boolean mayPlace(ItemStack stack){return false;}});addSlot(new Slot(machine,3,35,71));addSlot(new Slot(machine,4,80,71));addSlot(new Slot(machine,5,98,71));for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,9+y*9+x,8+x*18,121+y*18));for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,179));}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=5){if(!moveItemStackTo(stack,6,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,3,4,false))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(FlareEntity.isUpgradeItem(stack)){if(!moveItemStackTo(stack,4,6,false))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,1,2,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
    @Override public boolean stillValid(Player player){return machine instanceof FlareEntity flare?flare.stillValid(player):position.distToCenterSqr(player.position())<=144;}
}
