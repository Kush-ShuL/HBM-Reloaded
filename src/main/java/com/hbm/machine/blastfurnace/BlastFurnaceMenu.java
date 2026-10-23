package com.hbm.machine.blastfurnace;

import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact source five slots, playerY140 and original ContainerBase merge order. */
public final class BlastFurnaceMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;private final BlockPos pos;
    public BlastFurnaceMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private BlastFurnaceMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof BlastFurnaceBlockEntity be?be:new SimpleContainer(5),new SimpleContainerData(7),pos);}
    public BlastFurnaceMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){
        super(BlastFurnaceContent.MENU.get(),id);this.machine=machine;this.data=data;this.pos=pos;checkContainerSize(machine,5);checkContainerDataCount(data,7);machine.startOpen(player.player);
        addSlot(input(machine,0,80,81));addSlot(input(machine,1,80,27));addSlot(input(machine,2,80,45));addSlot(new GenericOutputSlot(player.player,machine,3,134,72));addSlot(new GenericOutputSlot(player.player,machine,4,134,90));
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+column*18,140+row*18));for(int column=0;column<9;column++)addSlot(new Slot(player,column,8+column*18,198));addDataSlots(data);
    }
    private static Slot input(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(index,stack);}};}
    private int bits(int start){return (data.get(start)&65535)|((data.get(start+1)&65535)<<16);}
    public BlockPos position(){return pos;}public int fuel(){return bits(0);}public float progress(){return Float.intBitsToFloat(bits(2));}public float speed(){return Float.intBitsToFloat(bits(4));}public boolean processing(){return data.get(6)!=0;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(!moveItemStackTo(stack,index<5?5:0,index<5?slots.size():5,index<5))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
