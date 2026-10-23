package com.hbm.railway.cart;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
/** Exact interleaved source pattern-slot topology; cursor is copied, never consumed or shift-transferred. */
public final class DestroyerCartMenu extends AbstractContainerMenu {
    public final Container container;
    private static Container read(Inventory inventory,RegistryFriendlyByteBuf data){var entity=inventory.player.level().getEntity(data.readInt());return entity instanceof SourceDestroyerCart cart?cart:new SimpleContainer(18);}
    public DestroyerCartMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,read(inventory,data));}
    public DestroyerCartMenu(int id,Inventory inventory,Container container){
        super(SourceCartContent.DESTROYER_MENU.get(),id);this.container=container;checkContainerSize(container,18);
        for(int row=0;row<3;row++)for(int col=0;col<3;col++){addSlot(new PatternSlot(container,col+row*3,10+col*18,17+row*18));addSlot(new PatternSlot(container,col+row*3+9,114+col*18,17+row*18));}
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
    }
    @Override public void clicked(int index,int button,ClickType mode,Player player){if(index>=0&&index<18){getSlot(index).set(getCarried());return;}super.clicked(index,button,mode,player);}
    @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
    @Override public boolean stillValid(Player player){return container.stillValid(player);}
    private static final class PatternSlot extends Slot {
        PatternSlot(Container container,int index,int x,int y){super(container,index,x,y);}
        @Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}
        @Override public boolean mayPickup(Player player){return false;}
        @Override public int getMaxStackSize(){return 1;}
    }
}
