package com.hbm.machine.reactor.zirnox;
import com.hbm.fluid.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original28+36 slots, including alternating input/output menu indices for the native coolant containers. */
public final class ZirnoxMenu extends AbstractContainerMenu {
    private final Container machine;private final BlockPos position;
    public ZirnoxMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private ZirnoxMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ZirnoxBlockEntity reactor?reactor:new SimpleContainer(28),pos);}
    public ZirnoxMenu(int id,Inventory player,Container machine,BlockPos pos){super(ZirnoxContent.MENU.get(),id);this.machine=machine;position=pos;machine.startOpen(player.player);int[][] points={{26,16},{62,16},{98,16},{8,34},{44,34},{80,34},{116,34},{26,52},{62,52},{98,52},{8,70},{44,70},{80,70},{116,70},{26,88},{62,88},{98,88},{8,106},{44,106},{80,106},{116,106},{26,124},{62,124},{98,124}};for(int i=0;i<24;i++)addSlot(new Slot(machine,i,points[i][0],points[i][1]));addSlot(new Slot(machine,24,143,124));addSlot(output(machine,26,143,142));addSlot(new Slot(machine,25,179,124));addSlot(output(machine,27,179,142));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,174+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,232));}
    private static Slot output(Container machine,int index,int x,int y){return new Slot(machine,index,x,y){@Override public boolean mayPlace(ItemStack stack){return false;}};}
    public BlockPos position(){return position;}public void control(boolean toggle,boolean vent){if(machine instanceof ZirnoxBlockEntity reactor)reactor.control(toggle,vent);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=27){if(!moveItemStackTo(stack,28,slots.size(),true))return ItemStack.EMPTY;}else if(FluidContainerRegistry.getFluidContent(stack,HbmFluids.CARBONDIOXIDE)>0){if(!moveItemStackTo(stack,24,26,false))return ItemStack.EMPTY;}else if(FluidContainerRegistry.getFluidContent(stack,HbmFluids.WATER)>0){if(!moveItemStackTo(stack,26,28,false))return ItemStack.EMPTY;}else if(!(stack.getItem() instanceof ZirnoxRod)||!moveItemStackTo(stack,0,24,true))return ItemStack.EMPTY;if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
