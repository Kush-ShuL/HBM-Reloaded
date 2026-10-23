package com.hbm.machine.chemicalfactory;
import com.hbm.item.battery.*;
import com.hbm.machine.oilmixer.MixerEntity;
import com.hbm.recipe.generic.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source32 machine slots/36 player slots; blueprint shift priority intentionally only reaches field1 as original. */
public final class ChemicalFactoryMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public ChemicalFactoryMenu(int id,Inventory player,RegistryFriendlyByteBuf data){this(id,player,data.readBlockPos());}
 private ChemicalFactoryMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof ChemicalFactoryEntity factory?factory:new SimpleContainer(32),pos);}
 public ChemicalFactoryMenu(int id,Inventory player,Container machine,BlockPos pos){super(ChemicalFactoryContent.MENU.get(),id);checkContainerSize(machine,32);this.machine=machine;position=pos;addSlot(input(machine,0,224,88));for(int i=0;i<3;i++)addSlot(input(machine,1+i,206,125+i*18));for(int g=0;g<4;g++){addSlot(input(machine,4+g*7,93,20+g*22));for(int i=0;i<3;i++)addSlot(input(machine,5+g*7+i,10+i*16,20+g*22));for(int i=0;i<3;i++)addSlot(new GenericOutputSlot(player.player,machine,8+g*7+i,139+i*16,20+g*22));}for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,26+col*18,134+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,26+col*18,192));}
 private static Slot input(Container machine,int index,int x,int y){return new Slot(machine,index,x,y){@Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(index,stack);}@Override public int getMaxStackSize(){return Math.max(machine.getMaxStackSize(),hasItem()?getItem().getCount():1);}};}
 public void selectRecipe(int index,String selection){if(machine instanceof ChemicalFactoryEntity factory)factory.selectRecipe(index,selection);}
 @Override public boolean stillValid(Player player){return machine instanceof ChemicalFactoryEntity factory?factory.stillValid(player):position.distToCenterSqr(player.position())<=64;}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var original=stack.copy();if(index<32){GenericOutputSlot.checkAchievements(player,stack);if(!moveItemStackTo(stack,32,slots.size(),true))return ItemStack.EMPTY;}else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,0,1,false))return ItemStack.EMPTY;}else if(stack.is(Blueprints.ITEM.get())){if(!moveItemStackTo(stack,4,5,false))return ItemStack.EMPTY;}else if(MixerEntity.isUpgradeItem(stack)){if(!moveItemStackTo(stack,1,4,false))return ItemStack.EMPTY;}else{boolean moved=false;for(int start:new int[]{5,12,19,26})if(moveItemStackTo(stack,start,start+3,false)){moved=true;break;}if(!moved)return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return original;}
}
