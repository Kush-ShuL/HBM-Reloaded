package com.hbm.machine.reactor.rbmk.channel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source one-slot176x186 layout and whole slotClick lock above200°C, including non-pickup modes. */
public final class RbmkChannelMenu extends AbstractContainerMenu {
    public final BlockPos position; private final Container machine;
    public RbmkChannelMenu(int id,Inventory player,RegistryFriendlyByteBuf buf) { this(id,player,buf.readBlockPos()); }
    private RbmkChannelMenu(int id,Inventory player,BlockPos pos) { this(id,player,player.player.level().getBlockEntity(pos) instanceof RbmkFuelChannelEntity channel?channel:new SimpleContainer(1),pos); }
    public RbmkChannelMenu(int id,Inventory player,Container machine,BlockPos pos) { super(RbmkFuelChannels.MENU.get(),id);this.position=pos;this.machine=machine;addSlot(new Slot(machine,0,80,45));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,104+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,162)); }
    @Override public void clicked(int slot,int button,ClickType type,Player player) { if(slot==0&&!player.getAbilities().instabuild&&machine instanceof RbmkFuelChannelEntity channel&&!channel.coldEnoughForManual())return;super.clicked(slot,button,type,player); }
    @Override public ItemStack quickMoveStack(Player player,int index) { if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var result=stack.copy();if(index==0){if(!player.getAbilities().instabuild&&machine instanceof RbmkFuelChannelEntity channel&&!channel.coldEnoughForManual())return ItemStack.EMPTY;if(!moveItemStackTo(stack,1,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return result; }
    @Override public boolean stillValid(Player player) { return true; } // Source ContainerRBMKRod.canInteractWith is intentionally unconditional.
}
