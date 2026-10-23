package com.hbm.machine.assemblyfactory;

import com.hbm.item.battery.*;
import com.hbm.machine.oilmixer.MixerEntity;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original60+36 slots, sixteen-pixel input grid and exact source shift-click quirks. */
public final class AssemblyFactoryMenu extends AbstractContainerMenu {
    public final BlockPos position;
    private final Container machine;

    public AssemblyFactoryMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
    private AssemblyFactoryMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof AssemblyFactoryEntity factory?factory:new SimpleContainer(60),pos);}
    public AssemblyFactoryMenu(int id,Inventory player,Container machine,BlockPos pos){
        super(AssemblyFactoryContent.MENU.get(),id);checkContainerSize(machine,60);this.machine=machine;position=pos;machine.startOpen(player.player);
        addSlot(input(machine,0,234,112));
        for(int i=0;i<3;i++)addSlot(input(machine,1+i,214,149+i*18));
        for(int group=0;group<4;group++){
            int x=(group%2)*109,y=(group/2)*56,base=group*14;
            addSlot(input(machine,4+base,25+x,54+y));
            for(int row=0;row<2;row++)for(int col=0;col<6;col++)addSlot(input(machine,5+base+row*6+col,7+x+col*16,20+y+row*16));
            addSlot(new GenericOutputSlot(player.player,machine,17+base,87+x,54+y));
        }
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,33+col*18,158+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(player,col,33+col*18,216));
    }
    private static Slot input(Container machine,int index,int x,int y){return new Slot(machine,index,x,y){
        @Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(index,stack);}
        @Override public int getMaxStackSize(){return Math.max(machine.getMaxStackSize(),hasItem()?getItem().getCount():1);}
    };}
    public void selectRecipe(int index,String selection){if(machine instanceof AssemblyFactoryEntity factory)factory.selectRecipe(index,selection);}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size())return ItemStack.EMPTY;
        var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var original=stack.copy();
        if(index<60){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,60,slots.size(),true))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}
        // Original four repeated identical else-if predicates only allow the first blueprint slot.
        else if(stack.is(Blueprints.ITEM.get())){if(!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;}
        else if(MixerEntity.isUpgradeItem(stack)){if(!moveItemStackTo(stack,1,4,false))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,5,17,false)&&!moveItemStackTo(stack,19,31,false)&&!moveItemStackTo(stack,33,46,false)&&!moveItemStackTo(stack,47,59,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;
    }
}
