package com.hbm.machine.reactor.rbmk.control;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Both original containers have no machine slots and no quick-move action. */
public final class RbmkControlMenu extends AbstractContainerMenu {
    public final BlockPos position;
    public RbmkControlMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    public RbmkControlMenu(int id,Inventory player,BlockPos pos){super(RbmkControlsContent.MENU.get(),id);position=pos;for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,104+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,162));}
    @Override public boolean stillValid(Player player){return true;}@Override public ItemStack quickMoveStack(Player player,int slot){return ItemStack.EMPTY;}
}
