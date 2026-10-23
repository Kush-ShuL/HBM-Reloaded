package com.hbm.machine.purex;

import com.hbm.item.battery.*;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original13+36 slots and original shift-click routing, including container return lanes. */
public final class PurexMenu extends AbstractContainerMenu {
    private final Container machine;private final ContainerData data;private final BlockPos pos;
    public PurexMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private PurexMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof PurexBlockEntity be?be:new SimpleContainer(13),new SimpleContainerData(14),pos);}
    public PurexMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){
        super(PurexContent.MENU.get(),id);checkContainerSize(machine,13);checkContainerDataCount(data,14);this.machine=machine;this.data=data;this.pos=pos;machine.startOpen(player.player);
        addSlot(input(machine,0,152,81));addSlot(input(machine,1,35,126));for(int i=0;i<2;i++)addSlot(input(machine,2+i,152,108+i*18));
        for(int i=0;i<3;i++)addSlot(input(machine,4+i,8+i*18,90));
        for(int row=0;row<3;row++)for(int col=0;col<2;col++)addSlot(new GenericOutputSlot(player.player,machine,7+row*2+col,80+col*18,36+row*18));
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+column*18,174+row*18));
        for(int column=0;column<9;column++)addSlot(new Slot(player,column,8+column*18,232));addDataSlots(data);
    }
    private static Slot input(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(index,stack);}};}
    private static Slot takeOnly(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return false;}};}
    public static boolean upgrade(ItemStack stack){return stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem;}
    private long bits(int start){long value=0;for(int i=0;i<4;i++)value|=(data.get(start+i)&65535L)<<(i*16);return value;}
    public BlockPos position(){return pos;}public long power(){return bits(0);}public long capacity(){return bits(4);}public double progress(){return Double.longBitsToDouble(bits(8));}public boolean processing(){return data.get(12)!=0;}public boolean restricted(){return data.get(13)!=0;}
    public void selectRecipe(int index,String selection){if(machine instanceof PurexBlockEntity be)be.selectRecipe(index,selection);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}@Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();
        if(index<13){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,13,slots.size(),true))return ItemStack.EMPTY;}
        else {int start=stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem?0:stack.getItem()==Blueprints.ITEM.get()?1:upgrade(stack)?2:4;int end=start==2?4:start==4?7:start+1;if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}
        if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;
    }
}
