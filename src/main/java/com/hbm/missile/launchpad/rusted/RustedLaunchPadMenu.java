package com.hbm.missile.launchpad.rusted;
import api.hbm.item.IDesignatorItem;
import com.hbm.recipe.generic.GenericOutputSlot;
import com.hbm.registry.HbmContent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Actual original4+36 slots and original shift-click role filter; ordinary GUI slots still accept manual arbitrary items. */
public final class RustedLaunchPadMenu extends AbstractContainerMenu {
    private final Container machine;private final BlockPos pos;
    public RustedLaunchPadMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private RustedLaunchPadMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof RustedLaunchPadBlockEntity be?be:new SimpleContainer(4),pos);}
    public RustedLaunchPadMenu(int id,Inventory player,Container machine,BlockPos pos){super(RustedLaunchPadContent.MENU.get(),id);checkContainerSize(machine,4);this.machine=machine;this.pos=pos;addSlot(new GenericOutputSlot(player.player,machine,0,26,72));addSlot(new Slot(machine,1,116,45));addSlot(new Slot(machine,2,134,45));addSlot(new Slot(machine,3,26,99));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,154+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,212));}
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int action){if(action!=0||!(machine instanceof RustedLaunchPadBlockEntity pad)||!pad.stillValid(player))return false;pad.releaseMissile();return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;Slot slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack stack=slot.getItem(),copy=stack.copy();if(index<=3){if(!moveItemStackTo(stack,4,slots.size(),true))return ItemStack.EMPTY;}else{int target;if(copy.getItem() instanceof IDesignatorItem)target=3;else if(copy.is(HbmContent.item("launch_code")))target=1;else if(copy.is(HbmContent.item("launch_key")))target=2;else return ItemStack.EMPTY;if(!moveItemStackTo(stack,target,target+1,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
