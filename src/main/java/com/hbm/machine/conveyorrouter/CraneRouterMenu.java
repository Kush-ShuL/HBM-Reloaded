package com.hbm.machine.conveyorrouter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Thirty source SlotPatterns: copying a cursor stack never consumes it; all quick transfer is disabled. */
public final class CraneRouterMenu extends AbstractContainerMenu {
    public final CraneRouterEntity router;
    public CraneRouterMenu(int id,Inventory inventory,RegistryFriendlyByteBuf data){this(id,inventory,resolve(inventory,data.readBlockPos()));}
    private static CraneRouterEntity resolve(Inventory inventory,BlockPos pos){return inventory.player.level().getBlockEntity(pos)instanceof CraneRouterEntity router?router:new CraneRouterEntity(pos,CraneRouterContent.BLOCK.get().defaultBlockState());}
    public CraneRouterMenu(int id,Inventory inventory,CraneRouterEntity router){
        super(CraneRouterContent.MENU.get(),id);this.router=router;
        for(int group=0;group<2;group++)for(int row=0;row<3;row++)for(int col=0;col<5;col++)addSlot(new Slot(router,col+group*15+row*5,34+col*18+group*98,17+row*26){
            @Override public boolean mayPickup(Player player){return false;}
            @Override public int getMaxStackSize(){return 1;}
            @Override public void set(ItemStack stack){super.set(stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));}
        });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,47+col*18,119+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,47+col*18,177));
    }
    @Override public void clicked(int index,int button,ClickType type,Player player){
        if(index<0||index>=30){super.clicked(index,button,type,player);return;}
        var slot=slots.get(index);
        if(button==1&&type==ClickType.PICKUP&&slot.hasItem()){if(!player.level().isClientSide)router.nextMode(index);}
        else{slot.set(getCarried());if(!player.level().isClientSide)router.initPattern(slot.getItem(),index);}
        broadcastChanges();
    }
    @Override public ItemStack quickMoveStack(Player player,int index){return ItemStack.EMPTY;}
    @Override public boolean canDragTo(Slot slot){return slot.container!=router&&super.canDragTo(slot);}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,Slot slot){return slot.container!=router&&super.canTakeItemForPickAll(stack,slot);}
    @Override public boolean stillValid(Player player){return router.stillValid(player);}
}
