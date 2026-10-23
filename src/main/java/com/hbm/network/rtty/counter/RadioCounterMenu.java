package com.hbm.network.rtty.counter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Original three SlotPattern ghosts: held stack is copied, never consumed; no quick transfer. */
public final class RadioCounterMenu extends AbstractContainerMenu {
 public final RadioCounterEntity counter;
 public RadioCounterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,resolve(inventory,buffer));}private static RadioCounterEntity resolve(Inventory inventory,RegistryFriendlyByteBuf buffer){var pos=buffer.readBlockPos();return inventory.player.level().getBlockEntity(pos) instanceof RadioCounterEntity counter?counter:new RadioCounterEntity(pos,RadioCounterContent.BLOCK.get().defaultBlockState());}
 public RadioCounterMenu(int id,Inventory inv,RadioCounterEntity counter){super(RadioCounterContent.MENU.get(),id);this.counter=counter;for(int i=0;i<3;i++)addSlot(new Slot(counter,i,138,18+44*i){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}@Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}});for(int i=0;i<3;i++)for(int j=0;j<9;j++)addSlot(new Slot(inv,j+i*9+9,12+j*18,156+i*18));for(int i=0;i<9;i++)addSlot(new Slot(inv,i,12+i*18,214));}
 @Override public void clicked(int index,int button,ClickType type,Player player){if(index<0||index>2){super.clicked(index,button,type,player);return;}var slot=slots.get(index);if(button==1&&type==ClickType.PICKUP&&slot.hasItem()){if(!player.level().isClientSide)counter.nextMode(index);}else{slot.set(getCarried());if(!player.level().isClientSide)counter.matcher.initPatternStandard(slot.getItem(),index);}counter.setChanged();broadcastChanges();}
 @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}@Override public boolean stillValid(Player player){return counter.stillValid(player);}
}
