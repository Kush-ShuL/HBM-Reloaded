package com.hbm.block.rebar;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Source1 ghost pattern+36 player slots. Pattern clicks copy one item and never consume or return it. */
public final class RebarPlacerMenu extends AbstractContainerMenu {
 private final Inventory player;private final int targetSlot;private final SimpleContainer pattern;
 public RebarPlacerMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readVarInt());}
 public RebarPlacerMenu(int id,Inventory player,int targetSlot){super(RebarContent.MENU.get(),id);this.player=player;this.targetSlot=targetSlot;pattern=new SimpleContainer(1);pattern.setItem(0,RebarPlacerItem.pattern(target(),player.player.registryAccess()));addSlot(new Slot(pattern,0,53,36){@Override public boolean mayPickup(Player player){return false;}@Override public int getMaxStackSize(){return 1;}});for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+column*18,100+row*18));for(int column=0;column<9;column++)addSlot(new Slot(player,column,8+column*18,158));}
 public ItemStack target(){return targetSlot>=0&&targetSlot<player.getContainerSize()?player.getItem(targetSlot):ItemStack.EMPTY;}
 public Component title(){var target=target();return target.has(DataComponents.CUSTOM_NAME)?target.getHoverName():Component.translatable("container.rebar");}
 @Override public void clicked(int index,int button,ClickType type,Player owner){if(index==0){var held=getCarried();pattern.setItem(0,held.isEmpty()?ItemStack.EMPTY:held.copyWithCount(1));RebarPlacerItem.pattern(target(),pattern.getItem(0),owner.registryAccess());broadcastChanges();return;}if(type==ClickType.SWAP&&button==targetSlot)return;if(index>=0&&index<slots.size()){var slot=getSlot(index);if(slot.container==player&&slot.getSlotIndex()==targetSlot)return;}super.clicked(index,button,type,owner);}
 @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}@Override public boolean stillValid(Player owner){return target().is(RebarContent.PLACER.get());}
}
