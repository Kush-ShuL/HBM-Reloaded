package com.hbm.machine.weapontable;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Original7mod+1gun container, real manager callbacks, config switching, source shift order and close ejection. */
public final class WeaponTableMenu extends AbstractContainerMenu {
    public final SimpleContainer mods=new SimpleContainer(7),gun=new SimpleContainer(1);
    private final Player owner;public int index;
    public WeaponTableMenu(int id,Inventory inventory,RegistryFriendlyByteBuf buffer){this(id,inventory);}
    public WeaponTableMenu(int id,Inventory inventory){
        super(WeaponTableContent.MENU.get(),id);owner=inventory.player;
        for(int i=0;i<7;i++)addSlot(new ModSlot(i,44+18*i,108));
        addSlot(new GunSlot());
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,158+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,216));
        addDataSlot(new DataSlot(){public int get(){return index;}public void set(int value){index=value;}});
    }
    public ItemStack weapon(){return gun.getItem(0);}
    public int configCount(){return weapon().getItem() instanceof ItemGunBaseNT item?item.getConfigCount():0;}
    private ItemStack[] upgradeStacks(){var values=new ItemStack[7];for(int i=0;i<values.length;i++)values[i]=mods.getItem(i);return values;}
    private void loadInstalled(ItemStack stack){var installed=XWeaponModManager.getUpgradeItems(stack,index);for(int i=0;i<Math.min(installed.length,7);i++)mods.setItem(i,installed[i].isEmpty()?ItemStack.EMPTY:installed[i].copyWithCount(1));}
    private void clearAttached(ItemStack stack,int configuration){for(int i=0;i<7;i++)if(XWeaponModManager.isApplicable(stack,mods.getItem(i),configuration,false))mods.setItem(i,ItemStack.EMPTY);}
    private void refreshInstalled(){if(!owner.level().isClientSide&&weapon().getItem() instanceof ItemGunBaseNT)XWeaponModManager.install(weapon(),index,upgradeStacks());}
    @Override public boolean clickMenuButton(Player player,int requested){
        if(player.level().isClientSide||!(weapon().getItem() instanceof ItemGunBaseNT item)||requested<0||requested>=item.getConfigCount())return false;
        var stack=weapon();XWeaponModManager.install(stack,index,upgradeStacks());clearAttached(stack,index);index=requested;loadInstalled(stack);broadcastChanges();return true;
    }
    @Override public boolean stillValid(Player player){return true;}
    @Override public void removed(Player player){
        super.removed(player);if(player.level().isClientSide)return;
        for(int i=0;i<7;i++){var stack=mods.removeItemNoUpdate(i);if(!stack.isEmpty())player.drop(stack,false);}
        var stack=gun.removeItemNoUpdate(0);if(!stack.isEmpty()){XWeaponModManager.uninstall(stack,index);player.drop(stack,false);}
    }
    private final class GunSlot extends Slot {
        GunSlot(){super(gun,0,8,108);}
        @Override public boolean mayPlace(ItemStack stack){return gun.isEmpty()&&stack.getItem() instanceof ItemGunBaseNT;}
        @Override public int getMaxStackSize(){return 1;}
        @Override public void setByPlayer(ItemStack stack,ItemStack previous){
            if(!stack.isEmpty()){WeaponTableMenu.this.index=0;loadInstalled(stack);}super.setByPlayer(stack,previous);
        }
        @Override public void onTake(Player player,ItemStack stack){
            if(!player.level().isClientSide&&stack.getItem() instanceof ItemGunBaseNT){XWeaponModManager.install(stack,WeaponTableMenu.this.index,upgradeStacks());clearAttached(stack,WeaponTableMenu.this.index);WeaponTableMenu.this.index=0;}
            super.onTake(player,stack);
        }
    }
    private final class ModSlot extends Slot {
        ModSlot(int slot,int x,int y){super(mods,slot,x,y);}
        @Override public boolean mayPlace(ItemStack stack){return weapon().getItem() instanceof ItemGunBaseNT&&XWeaponModManager.isApplicable(weapon(),stack,WeaponTableMenu.this.index,true);}
        // A saved upgrade is one handler identity: accepting64 would destroy the other63 on gun removal in the old source.
        @Override public int getMaxStackSize(){return 1;}
        @Override public void setByPlayer(ItemStack stack,ItemStack previous){super.setByPlayer(stack,previous);refreshInstalled();}
        @Override public void onTake(Player player,ItemStack stack){super.onTake(player,stack);refreshInstalled();}
    }
    @Override public ItemStack quickMoveStack(Player player,int selected){
        if(selected<0||selected>=slots.size())return ItemStack.EMPTY;var slot=slots.get(selected);if(!slot.hasItem())return ItemStack.EMPTY;
        var stack=slot.getItem();var copy=stack.copy();
        if(selected==7){
            boolean space=false;for(int at=8;at<slots.size();at++)if(!slots.get(at).hasItem()&&slots.get(at).mayPlace(stack)){space=true;break;}if(!space)return ItemStack.EMPTY;
            // Native split/merge makes an emptied ItemStack report AIR. Commit to the real outgoing stack before moving it.
            var outgoing=stack.copy();XWeaponModManager.install(outgoing,index,upgradeStacks());var installed=outgoing.copy();
            if(!mergeSource(outgoing,8,slots.size(),true))return ItemStack.EMPTY;
            gun.setItem(0,ItemStack.EMPTY);clearAttached(installed,index);index=0;slot.setChanged();return copy;
        }
        if(selected<7){if(!mergeSource(stack,8,slots.size(),true))return ItemStack.EMPTY;slot.onTake(player,stack);}
        else if(stack.getItem() instanceof ItemGunBaseNT){if(!mergeSource(stack,7,8,false))return ItemStack.EMPTY;}
        else if(!mergeSource(stack,0,7,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
    /** Source InventoryUtil checks mayPlace while merging existing stacks, too. */
    private boolean mergeSource(ItemStack stack,int start,int end,boolean reverse){
        boolean moved=false;int step=reverse?-1:1,at=reverse?end-1:start;
        if(stack.isStackable())while(!stack.isEmpty()&&(reverse?at>=start:at<end)){
            var slot=slots.get(at);var present=slot.getItem();int maximum=Math.min(stack.getMaxStackSize(),slot.getMaxStackSize());
            if(!present.isEmpty()&&slot.mayPlace(stack.copyWithCount(Math.min(stack.getCount(),maximum)))&&ItemStack.isSameItemSameComponents(present,stack)&&present.getCount()<maximum){int amount=Math.min(maximum-present.getCount(),stack.getCount());stack.shrink(amount);present.grow(amount);slot.setByPlayer(present);moved=true;}at+=step;
        }
        at=reverse?end-1:start;while(!stack.isEmpty()&&(reverse?at>=start:at<end)){
            var slot=slots.get(at);int amount=Math.min(stack.getCount(),Math.min(stack.getMaxStackSize(),slot.getMaxStackSize()));
            if(!slot.hasItem()&&slot.mayPlace(stack.copyWithCount(amount))){slot.setByPlayer(stack.split(amount));moved=true;}at+=step;
        }return moved;
    }
}
