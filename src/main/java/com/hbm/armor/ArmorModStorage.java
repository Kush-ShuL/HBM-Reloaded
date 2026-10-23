package com.hbm.armor;

import com.hbm.hazard.HazardContext;
import com.hbm.hazard.HazardStoredStacks;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Source nine-slot ntm_armor_mods storage, with live modern component stacks and original named stacks. */
public final class ArmorModStorage {
    public static final int HELMET=0,CHEST=1,LEGS=2,BOOTS=3,SERVOS=4,CLADDING=5,KEVLAR=6,EXTRA=7,BATTERY=8,SLOTS=9;
    public static final String COMPOUND="ntm_armor_mods", SLOT="mod_slot_";
    private static Map<String,String> aliases=Map.of();
    private ArmorModStorage(){}
    static void configureAliases(Map<String,String> aliases){ArmorModStorage.aliases=Map.copyOf(aliases);}
    static HolderLookup.Provider lookup(HolderLookup.Provider supplied){return supplied!=null?supplied:RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);}
    public static boolean isApplicable(ItemStack armor,ItemStack mod){
        if(!(armor.getItem() instanceof ArmorItem item)||!(mod.getItem() instanceof ArmorModItem upgrade))return false;
        return switch(item.getEquipmentSlot()){case HEAD->upgrade.helmet;case CHEST->upgrade.chestplate;case LEGS->upgrade.leggings;case FEET->upgrade.boots;default->false;};
    }
    static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    static void data(ItemStack stack,CompoundTag data){stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    public static boolean hasMods(ItemStack armor){return !armor.isEmpty()&&data(armor).contains(COMPOUND);}
    public static void applyMod(ItemStack armor,ItemStack mod,HolderLookup.Provider lookup){
        if(!(mod.getItem() instanceof ArmorModItem item))throw new IllegalArgumentException("Not an actual armor modification");
        var data=data(armor);var mods=data.getCompound(COMPOUND);mods.put(SLOT+item.type,mod.save(lookup(lookup)));data.put(COMPOUND,mods);data(armor,data);
    }
    public static void removeMod(ItemStack armor,int slot){
        if(armor.isEmpty())return;
        var data=data(armor);var mods=data.getCompound(COMPOUND);mods.remove(SLOT+slot);
        if(mods.isEmpty())data.remove(COMPOUND);else data.put(COMPOUND,mods);data(armor,data);
    }
    public static void clearMods(ItemStack armor){if(armor.isEmpty()||!armor.has(DataComponents.CUSTOM_DATA))return;var data=data(armor);data.remove(COMPOUND);data(armor,data);}
    public static ItemStack pryMod(ItemStack armor,int slot,HolderLookup.Provider lookup){
        if(!hasMods(armor))return ItemStack.EMPTY;
        var compound=data(armor).getCompound(COMPOUND).getCompound(SLOT+slot);
        try {
            var stack=HazardContext.withLookup(lookup(lookup),()->HazardStoredStacks.parse(compound,aliases));
            if(stack.isEmpty())removeMod(armor,slot);
            return stack;
        }catch(RuntimeException unresolved){
            // Numeric legacy registry IDs require a world ID map. Keep those
            // exact bytes for the future importer; never grant guessed protection.
            return ItemStack.EMPTY;
        }
    }
    public static ItemStack[] pryMods(ItemStack armor,HolderLookup.Provider lookup){var result=new ItemStack[SLOTS];for(int slot=0;slot<SLOTS;slot++)result[slot]=pryMod(armor,slot,lookup);return result;}
    static ItemStack parse(CompoundTag compound,HolderLookup.Provider lookup){
        try{return HazardContext.withLookup(lookup(lookup),()->HazardStoredStacks.parse(compound,aliases));}catch(RuntimeException unresolved){return ItemStack.EMPTY;}
    }
}
