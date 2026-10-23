package com.hbm.machine.reactor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source inventory accepts manual placement in every fuel slot; automation keeps its original slot0 rule. */
public final class ResearchReactorMenu extends AbstractContainerMenu {
    private final Container reactor;private final BlockPos pos;
    public ResearchReactorMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private ResearchReactorMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof ResearchReactorBlockEntity be?be:new SimpleContainer(12),pos);}
    public ResearchReactorMenu(int id,Inventory inventory,Container reactor,BlockPos pos){super(ReactorContent.RESEARCH_MENU.get(),id);this.reactor=reactor;this.pos=pos;checkContainerSize(reactor,12);reactor.startOpen(inventory.player);
        int[][] coordinates={{95,22},{131,22},{77,40},{113,40},{149,40},{95,58},{131,58},{77,76},{113,76},{149,76},{95,94},{131,94}};
        for(int i=0;i<12;i++)addSlot(new Slot(reactor,i,coordinates[i][0],coordinates[i][1]));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,140+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,198));
    }
    public BlockPos position(){return pos;}
    public void setTarget(double target){if(reactor instanceof ResearchReactorBlockEntity machine)machine.setTarget(target);}
    @Override public boolean stillValid(Player player){return reactor.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);reactor.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=12){if(!moveItemStackTo(stack,13,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof PlateFuelItem){if(!moveItemStackTo(stack,0,12,true))return ItemStack.EMPTY;}else return ItemStack.EMPTY;if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
