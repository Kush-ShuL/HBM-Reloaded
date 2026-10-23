package com.hbm.machine.crystallizer;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.centrifuge.OverdriveUpgradeItem;
import com.hbm.machine.electricfurnace.FurnaceUpgradeItem;
import com.hbm.recipe.generic.GenericOutputSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** True8+36 source slots, output crafting hooks, upgrade-only lanes and exact shift routing. */
public final class CrystallizerMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;private final ContainerData data;
 public CrystallizerMenu(int id,Inventory player,RegistryFriendlyByteBuf buf){this(id,player,buf.readBlockPos());}
 private CrystallizerMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof CrystallizerBlockEntity be?be:new SimpleContainer(8),new SimpleContainerData(4),pos);}
 public CrystallizerMenu(int id,Inventory player,Container machine,ContainerData data,BlockPos pos){super(CrystallizerContent.MENU.get(),id);this.position=pos;this.machine=machine;this.data=data;checkContainerSize(machine,8);checkContainerDataCount(data,4);machine.startOpen(player.player);addSlot(new Slot(machine,0,62,45));addSlot(new Slot(machine,1,152,72));addSlot(new GenericOutputSlot(player.player,machine,2,113,45));addSlot(new Slot(machine,3,17,18));addSlot(new GenericOutputSlot(player.player,machine,4,17,54));addSlot(upgradeSlot(machine,5,80,18));addSlot(upgradeSlot(machine,6,98,18));addSlot(new Slot(machine,7,35,72));for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,9+row*9+col,8+col*18,122+row*18));for(int col=0;col<9;col++)addSlot(new Slot(player,col,8+col*18,180));addDataSlots(data);}
 public static boolean upgrade(ItemStack stack){return stack.getItem() instanceof FurnaceUpgradeItem||stack.getItem() instanceof EffectUpgradeItem||stack.getItem() instanceof OverdriveUpgradeItem;}
 private static Slot upgradeSlot(Container container,int index,int x,int y){return new Slot(container,index,x,y){@Override public boolean mayPlace(ItemStack stack){return upgrade(stack);}};}
 public long power(){long value=0;for(int i=0;i<4;i++)value|=(data.get(i)&65535L)<<(i*16);return value;}
 @Override public boolean stillValid(Player p){return machine.stillValid(p);}@Override public void removed(Player p){super.removed(p);machine.stopOpen(p);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=getSlot(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();GenericOutputSlot.checkAchievements(player,stack);if(index<=7){if(!moveItemStackTo(stack,8,slots.size(),true))return ItemStack.EMPTY;}else{int start=stack.getItem() instanceof HbmBattery?1:stack.getItem() instanceof FluidIdentifier?7:upgrade(stack)?5:0,end=start==5?7:start+1;if(!moveItemStackTo(stack,start,end,false))return ItemStack.EMPTY;}if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;}
}
