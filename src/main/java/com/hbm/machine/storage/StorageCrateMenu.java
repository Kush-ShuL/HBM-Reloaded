package com.hbm.machine.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Source ContainerCrateBase transfer routing and protected currently held crate slot. */
public class StorageCrateMenu extends AbstractContainerMenu {
    private record Opening(boolean held,StorageCrateKind kind,BlockPos pos){}
    private static Opening read(RegistryFriendlyByteBuf data){boolean held=data.readBoolean();var kind=data.readEnum(StorageCrateKind.class);return new Opening(held,kind,held?null:data.readBlockPos());}
    public final Container container;public final StorageCrateKind kind;public final BlockPos position;private final int lockedHotbar;
    public StorageCrateMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,read(data));}
    private StorageCrateMenu(int id,Inventory inventory,Opening opening){this(id,inventory,opening.held?new HeldCrateInventory(inventory.player,inventory.player.getMainHandItem(),opening.kind):inventory.player.level().getBlockEntity(opening.pos) instanceof StorageCrateBlockEntity crate?crate:new SimpleContainer(opening.kind.slots),opening.kind,opening.pos);}
    public StorageCrateMenu(int id,Inventory inventory,Container container,StorageCrateKind kind,BlockPos pos){this(StorageCrateContent.MENU.get(),id,inventory,container,kind,pos);}
    /** Actual entity-backed source crate menu reuses these same slots/routing with its own complete entity-ID MenuType. */
    protected StorageCrateMenu(MenuType<?> type,int id,Inventory inventory,Container container,StorageCrateKind kind,BlockPos pos){
        super(type,id);this.container=container;this.kind=kind;position=pos;lockedHotbar=container instanceof HeldCrateInventory?inventory.selected:-1;checkContainerSize(container,kind.slots);container.startOpen(inventory.player);
        for(int row=0;row<kind.rows;row++)for(int col=0;col<kind.columns;col++)addSlot(new Slot(container,col+row*kind.columns,kind.storageX+col*18,18+row*18));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new SourcePlayerSlot(inventory,col+row*9+9,kind.playerX+col*18,kind.playerY+row*18,false));
        for(int col=0;col<9;col++)addSlot(new SourcePlayerSlot(inventory,col,kind.playerX+col*18,kind.hotbarY,col==lockedHotbar));
    }
    @Override public void clicked(int slot,int button,ClickType type,Player player){if(lockedHotbar>=0&&(slot==kind.slots+27+lockedHotbar||type==ClickType.SWAP&&button==lockedHotbar))return;super.clicked(slot,button,type,player);}
    @Override public boolean stillValid(Player player){return container.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);container.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;var stack=slot.getItem();var copy=stack.copy();if(index<kind.slots){if(!mergeSource(stack,kind.slots,slots.size(),true))return ItemStack.EMPTY;}else if(!mergeSource(stack,0,kind.slots,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return copy;}
    /** Original InventoryUtil.mergeItemStack checks insertion even while merging existing stacks. */
    private boolean mergeSource(ItemStack stack,int start,int end,boolean reverse){
        boolean success=false;int step=reverse?-1:1,index=reverse?end-1:start;
        if(stack.isStackable())while(!stack.isEmpty()&&(reverse?index>=start:index<end)){
            var slot=slots.get(index);var current=slot.getItem();if(!current.isEmpty()){
                int max=Math.min(stack.getMaxStackSize(),slot.getMaxStackSize()),toRemove=Math.min(stack.getCount(),max);
                if(slot.mayPlace(stack.copyWithCount(toRemove))&&ItemStack.isSameItemSameComponents(current,stack)){
                    int size=current.getCount()+stack.getCount();if(size<=max){stack.setCount(0);current.setCount(size);slot.setByPlayer(current);success=true;}
                    else if(current.getCount()<max){stack.shrink(max-current.getCount());current.setCount(max);slot.setByPlayer(current);success=true;}
                }
            }index+=step;
        }
        index=reverse?end-1:start;while(!stack.isEmpty()&&(reverse?index>=start:index<end)){
            var slot=slots.get(index);if(!slot.hasItem()){
                int max=Math.min(stack.getMaxStackSize(),slot.getMaxStackSize()),toRemove=Math.min(stack.getCount(),max);
                if(slot.mayPlace(stack.copyWithCount(toRemove))){slot.setByPlayer(stack.split(toRemove));success=true;}
            }index+=step;
        }
        return success;
    }
    private static final class SourcePlayerSlot extends Slot {
        private final boolean locked;SourcePlayerSlot(Container container,int index,int x,int y,boolean locked){super(container,index,x,y);this.locked=locked;}
        @Override public boolean mayPlace(ItemStack stack){return !locked&&container.canPlaceItem(getContainerSlot(),stack);}
        @Override public boolean mayPickup(Player player){return !locked;}
        @Override public int getMaxStackSize(){return Math.max(container.getMaxStackSize(),hasItem()?getItem().getCount():1);}
    }
}
