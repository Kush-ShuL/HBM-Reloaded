package com.hbm.hazard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Original nine-slot armor-mod NBT; only actually implemented armor-mod items execute hazards. */
final class HazardArmorMods {
    private static Map<String,String> aliases=Map.of();
    private HazardArmorMods(){}
    static void configure(Map<String,String> aliases){HazardArmorMods.aliases=Map.copyOf(aliases);}
    static Iterable<ItemStack> read(ItemStack armor,LivingEntity holder){
        List<ItemStack> result=new ArrayList<>();
        var custom=armor.get(DataComponents.CUSTOM_DATA);if(custom==null)return result;
        var mods=custom.copyTag().getCompound("ntm_armor_mods");
        for(int slot=0;slot<9;slot++){
            try{
                var stack=HazardStoredStacks.parse(mods.getCompound("mod_slot_"+slot),aliases);
                if(!stack.isEmpty()&&stack.getItem() instanceof HazardArmorModItem)result.add(stack);
            }catch(HazardStateUnavailable legacyUnavailable){
                // Unbound/legacy numeric items cannot be a live modern ItemArmorMod.
            }
        }
        return result;
    }
}
