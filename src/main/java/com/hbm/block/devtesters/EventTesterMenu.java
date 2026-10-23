package com.hbm.block.devtesters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Stable48native slots mapped over the one400source storage. Native button/DataSlot/fullstate replace obsolete clickmode=-666. */
public final class EventTesterMenu extends AbstractContainerMenu {
    public static final int SCROLL_BOUNDS=43;public final Container storage;public final BlockPos position;private int row;
    public EventTesterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,data.readBlockPos());}
    private EventTesterMenu(int id,Inventory inventory,BlockPos position){this(id,inventory,inventory.player.level().getBlockEntity(position) instanceof EventTesterEntity tile?tile:new SimpleContainer(400),position);}
    public EventTesterMenu(int id,Inventory inventory,Container storage,BlockPos position){
        super(DevTestersContent.MENU.get(),id);this.storage=storage;this.position=position;checkContainerSize(storage,400);var window=new Window();
        for(int y=0;y<6;y++)for(int x=0;x<8;x++)addSlot(new Slot(window,x+y*8,8+x*18,18+y*18));
        for(int y=0;y<3;y++)for(int x=0;x<9;x++)addSlot(new Slot(inventory,x+y*9+9,8+x*18,140+y*18));
        for(int x=0;x<9;x++)addSlot(new Slot(inventory,x,8+x*18,198));
        addDataSlot(new DataSlot(){@Override public int get(){return row;}@Override public void set(int value){row=Mth.clamp(value,0,SCROLL_BOUNDS);}});
    }
    public int scroll(){return row;}
    public void predictScroll(int value){row=Mth.clamp(value,0,SCROLL_BOUNDS);}
    @Override public boolean clickMenuButton(Player player,int value){if(value<0||value>SCROLL_BOUNDS||!stillValid(player))return false;row=value;broadcastFullState();return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
    @Override public boolean stillValid(Player player){return storage.stillValid(player);}
    private final class Window implements Container {
        private int actual(int slot){return slot+row*8;}
        @Override public int getContainerSize(){return 48;}
        @Override public boolean isEmpty(){for(int slot=0;slot<48;slot++)if(!getItem(slot).isEmpty())return false;return true;}
        @Override public ItemStack getItem(int slot){return storage.getItem(actual(slot));}
        @Override public ItemStack removeItem(int slot,int count){return storage.removeItem(actual(slot),count);}
        @Override public ItemStack removeItemNoUpdate(int slot){return storage.removeItemNoUpdate(actual(slot));}
        @Override public void setItem(int slot,ItemStack stack){storage.setItem(actual(slot),stack);}
        @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
        @Override public int getMaxStackSize(){return 64;}
        @Override public void setChanged(){storage.setChanged();}
        @Override public boolean stillValid(Player player){return storage.stillValid(player);}
        @Override public void clearContent(){for(int slot=0;slot<48;slot++)setItem(slot,ItemStack.EMPTY);}
    }
}
