package com.hbm.weapon.sedna.gun.energy.bag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact two-by-four bag grid, player grid, shift-click ranges and held-bag lock. */
public final class AmmoBagMenu extends AbstractContainerMenu {
    public final AmmoBagInventory bag;private final Inventory inventory;private final InteractionHand hand;
    public AmmoBagMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory,buffer.readEnum(InteractionHand.class));}
    public AmmoBagMenu(int id,Inventory inventory,InteractionHand hand){super(AmmoBagContent.AMMO_BAG_MENU.get(),id);this.inventory=inventory;this.hand=hand;bag=new AmmoBagInventory(inventory.player.getItemInHand(hand),inventory.player.registryAccess());
        for(int y=0;y<2;y++)for(int x=0;x<4;x++)addSlot(new Slot(bag,x+y*4,53+x*18,18+y*18){@Override public boolean mayPlace(ItemStack stack){return bag.canPlaceItem(getContainerSlot(),stack);}@Override public int getMaxStackSize(){return Math.max(bag.getMaxStackSize(),hasItem()?getItem().getCount():1);}});
        for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,82+y*18));
        for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,140));
    }
    @Override public boolean stillValid(Player player){return player.getItemInHand(hand)==bag.box;}
    @Override public void clicked(int index,int button,ClickType type,Player player){if(type==ClickType.SWAP&&(hand==InteractionHand.MAIN_HAND?button==inventory.selected:button==40))return;if(hand==InteractionHand.MAIN_HAND&&index==inventory.selected+27+8)return;super.clicked(index,button,type,player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var item=slot.getItem();var copy=item.copy();if(index<8){if(!moveItemStackTo(item,8,slots.size(),true))return ItemStack.EMPTY;}else if(!moveItemStackTo(item,0,8,false))return ItemStack.EMPTY;if(item.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,item);return copy;}
    @Override public void removed(Player player){super.removed(player);bag.setChanged();}
}
