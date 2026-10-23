package com.hbm.machine.lambda;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original seven SlotNonRetarded slots: actual source slot predicates apply to manual insertion too. */
public final class LambdaMenu extends AbstractContainerMenu {
    public final Container machine;private final BlockPos pos;
    public LambdaMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private LambdaMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof LambdaEntity m?m:new SimpleContainer(7),pos);}
    public LambdaMenu(int id,Inventory inventory,Container machine,BlockPos pos){super(LambdaContent.MENU.get(),id);checkContainerSize(machine,7);this.machine=machine;this.pos=pos;
        int[][] xy={{35,17},{53,17},{107,80},{107,98},{125,80},{125,98},{89,80}};
        for(int i=0;i<7;i++)addSlot(new Slot(machine,i,xy[i][0],xy[i][1]){@Override public boolean mayPlace(ItemStack stack){return container.canPlaceItem(getContainerSlot(),stack);}@Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}});
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(inventory,column+row*9+9,8+column*18,144+row*18));for(int column=0;column<9;column++)addSlot(new Slot(inventory,column,8+column*18,202));
    }
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public boolean clickMenuButton(Player player,int button){if(!(machine instanceof LambdaEntity launcher)||!launcher.stillValid(player)||button<0||button>2)return false;if(button<2)launcher.autolaunch=button==1;else if(launcher.canLaunch())launcher.countdown=LambdaEntity.COUNTDOWN_DURATION;launcher.setChanged();return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var result=stack.copy();if(index<7){if(!moveItemStackTo(stack,7,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(stack,0,7,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return result;}
}
