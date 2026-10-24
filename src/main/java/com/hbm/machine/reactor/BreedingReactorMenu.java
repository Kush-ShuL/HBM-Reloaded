package com.hbm.machine.reactor;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original input/output coordinates, source shift-click route and GenericOutputSlot achievements. */
public final class BreedingReactorMenu extends AbstractContainerMenu {
    private final Container reactor;private final BlockPos pos;
    public BreedingReactorMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readBlockPos());}
    private BreedingReactorMenu(int id,Inventory inventory,BlockPos pos){this(id,inventory,inventory.player.level().getBlockEntity(pos) instanceof BreedingReactorBlockEntity be?be:new SimpleContainer(2),pos);}
    public BreedingReactorMenu(int id,Inventory inventory,Container reactor,BlockPos pos){super(ReactorContent.BREEDING_MENU.get(),id);this.reactor=reactor;this.pos=pos;checkContainerSize(reactor,2);reactor.startOpen(inventory.player);addSlot(new Slot(reactor,0,35,35));addSlot(new GenericOutputSlot(inventory.player,reactor,1,125,35));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,84+row*18));for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));}
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return reactor.stillValid(player);}@Override public void removed(Player player){super.removed(player);reactor.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<=2){if(!moveItemStackTo(stack,2,slots.size(),true))return ItemStack.EMPTY;}else{if(!(stack.getItem() instanceof com.hbm.bomb.nuclear.NuclearBreedingRod)||!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
}
