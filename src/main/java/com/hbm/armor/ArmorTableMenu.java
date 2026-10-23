package com.hbm.armor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/** Source fifty slots, temporary inventory, original transfer/close behavior. */
public final class ArmorTableMenu extends AbstractContainerMenu {
    public final SimpleContainer upgrades=new SimpleContainer(ArmorModStorage.SLOTS),armor=new SimpleContainer(1);
    private final Player owner;
    public ArmorTableMenu(int id,Inventory inventory){
        super(HazmatContent.MENU.get(),id);owner=inventory.player;
        int[][] locations={{48,27},{84,27},{120,27},{156,45},{156,81},{120,99},{84,99},{48,99},{30,63}};
        for(int slot=0;slot<locations.length;slot++)addSlot(new UpgradeSlot(slot,locations[slot][0],locations[slot][1]));
        addSlot(new Slot(armor,0,66,63){
            @Override public boolean mayPlace(ItemStack stack){return stack.getItem() instanceof ArmorItem;}
            @Override public void set(ItemStack stack){if(!stack.isEmpty()){ItemStack[] mods=ArmorModStorage.pryMods(stack,owner.level().registryAccess());for(int index=0;index<mods.length;index++)upgrades.setItem(index,mods[index]);}super.set(stack);}
            @Override public void onTake(Player player,ItemStack taken){super.onTake(player,taken);for(int slot=0;slot<ArmorModStorage.SLOTS;slot++)if(ArmorModStorage.isApplicable(taken,upgrades.getItem(slot)))upgrades.setItem(slot,ItemStack.EMPTY);}
        });
        EquipmentSlot[] equipped={EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
        ResourceLocation[] icons={oldArmorIcon("helmet"),oldArmorIcon("chestplate"),oldArmorIcon("leggings"),oldArmorIcon("boots")};
        for(int index=0;index<4;index++){final int slot=index;addSlot(new Slot(inventory,39-index,5,36+index*18){
            @Override public int getMaxStackSize(){return 1;}
            @Override public boolean mayPlace(ItemStack stack){return stack.canEquip(equipped[slot],owner);}
            @Override public Pair<ResourceLocation,ResourceLocation> getNoItemIcon(){return Pair.of(InventoryMenu.BLOCK_ATLAS,icons[slot]);}
        });}
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(inventory,column+row*9+9,30+column*18,140+row*18));
        for(int column=0;column<9;column++)addSlot(new Slot(inventory,column,30+column*18,198));
    }
    private static ResourceLocation oldArmorIcon(String name){return ResourceLocation.fromNamespaceAndPath("hbm","item/empty_armor_slot_"+name);}
    @Override public boolean stillValid(Player player){return true;}
    @Override public ItemStack quickMoveStack(Player player,int index){
        Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        ItemStack remaining=slot.getItem(),original=remaining.copy();
        if(index<=ArmorModStorage.SLOTS){
            if(index!=ArmorModStorage.SLOTS||!moveItemStackTo(remaining,10,14,false))if(!moveItemStackTo(remaining,14,slots.size(),true))return ItemStack.EMPTY;
            // Original ItemStack retains its item when count reaches zero. Pass
            // the saved identity because modern empty stacks hide getItem().
            slot.onTake(player,original);
        }else if(remaining.getItem() instanceof ArmorItem){if(!moveItemStackTo(remaining,9,10,false))return ItemStack.EMPTY;}
        else if(remaining.getItem() instanceof ArmorModItem mod){if(mod.type<0||mod.type>=9||!slots.get(mod.type).mayPlace(remaining)||!moveItemStackTo(remaining,mod.type,mod.type+1,false))return ItemStack.EMPTY;}
        else return ItemStack.EMPTY;
        if(remaining.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();
        return original;
    }
    @Override public void removed(Player player){
        super.removed(player);
        if(!player.level().isClientSide){for(int slot=0;slot<ArmorModStorage.SLOTS;slot++){ItemStack mod=upgrades.removeItemNoUpdate(slot);if(!mod.isEmpty()){player.drop(mod,false);ArmorModStorage.removeMod(armor.getItem(0),slot);}}ItemStack piece=armor.removeItemNoUpdate(0);if(!piece.isEmpty())player.drop(piece,false);}
    }
    private final class UpgradeSlot extends Slot {
        private final int sourceSlot;
        UpgradeSlot(int index,int x,int y){super(upgrades,index,x,y);sourceSlot=index;}
        @Override public boolean mayPlace(ItemStack stack){return !armor.getItem(0).isEmpty()&&ArmorModStorage.isApplicable(armor.getItem(0),stack)&&((ArmorModItem)stack.getItem()).type==sourceSlot;}
        @Override public void set(ItemStack stack){super.set(stack);if(!stack.isEmpty()&&ArmorModStorage.isApplicable(armor.getItem(0),stack))ArmorModStorage.applyMod(armor.getItem(0),stack,owner.level().registryAccess());}
        @Override public void onTake(Player player,ItemStack stack){super.onTake(player,stack);ArmorModStorage.removeMod(armor.getItem(0),sourceSlot);}
    }
}
