package com.hbm.item.casingbag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Original ContainerCasingBag 5x3 grid, exact source coordinates, extract-only transfer, and open-item lock. */
public final class CasingBagMenu extends AbstractContainerMenu {
    public final CasingBagInventory bag;
    private final Inventory inventory;
    private final InteractionHand hand;
    public CasingBagMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer) { this(id,inventory,buffer.readEnum(InteractionHand.class)); }
    public CasingBagMenu(int id,Inventory inventory,InteractionHand hand) {
        super(CasingBagContent.MENU.get(),id);this.inventory=inventory;this.hand=hand;
        bag=new CasingBagInventory(inventory.player.getItemInHand(hand),inventory.player.registryAccess());
        bag.startOpen(inventory.player);
        for(int y=0;y<3;y++) for(int x=0;x<5;x++) addSlot(new Slot(bag,x+y*5,44+x*18,18+y*18) {
            @Override public boolean mayPlace(ItemStack item) { return bag.canPlaceItem(getContainerSlot(),item); }
            @Override public int getMaxStackSize() { return Math.max(bag.getMaxStackSize(),hasItem()?getItem().getCount():1); }
        });
        for(int y=0;y<3;y++) for(int x=0;x<9;x++) addSlot(new Slot(inventory,x+y*9+9,8+x*18,100+y*18));
        for(int x=0;x<9;x++) addSlot(new Slot(inventory,x,8+x*18,158));
    }
    @Override public boolean stillValid(Player player) { return bag.stillValid(player) && player.getItemInHand(hand)==bag.box; }
    @Override public void clicked(int index,int button,ClickType type,Player player) {
        if(type==ClickType.SWAP && (hand==InteractionHand.MAIN_HAND?button==inventory.selected:button==40)) return;
        if(hand==InteractionHand.MAIN_HAND && index==inventory.selected+27+15) return;
        super.clicked(index,button,type,player);
    }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        if(index<0 || index>=15) return ItemStack.EMPTY;
        var slot=slots.get(index);if(!slot.hasItem()) return ItemStack.EMPTY;
        var item=slot.getItem();var copy=item.copy();
        if(!moveItemStackTo(item,15,slots.size(),true)) return ItemStack.EMPTY;
        if(item.isEmpty()) slot.set(ItemStack.EMPTY);else slot.setChanged();
        slot.onTake(player,item);return copy;
        // Source InventoryUtil checks isItemValid in occupied AND empty slots;
        // every bag slot rejects placement, so the source player->bag shift branch always fails.
    }
    @Override public void removed(Player player) { super.removed(player);bag.stopOpen(player); }
}
