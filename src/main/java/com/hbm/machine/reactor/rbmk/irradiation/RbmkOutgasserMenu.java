package com.hbm.machine.reactor.rbmk.irradiation;
import com.hbm.advancement.HbmAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original bare inputSlot, real crafting output callbacks and source unusual output-shift-to-input behavior. */
public final class RbmkOutgasserMenu extends AbstractContainerMenu {
    public final BlockPos position;
    public RbmkOutgasserMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,new SimpleContainer(2),data.readBlockPos());}
    public RbmkOutgasserMenu(int id,Inventory player,Container machine,BlockPos pos){super(RbmkOutgasserContent.MENU.get(),id);position=pos;addSlot(new Slot(machine,0,48,45));addSlot(new com.hbm.recipe.generic.GenericOutputSlot(player.player,machine,1,112,69));for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(player,9+j+i*9,8+j*18,104+i*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,162));}
    @Override public boolean stillValid(Player player){return true;}@Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index==0){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
