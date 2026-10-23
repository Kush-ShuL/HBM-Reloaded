package com.hbm.machine.pilelegacy;

import com.hbm.hazard.HbmHazards;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Actual shared material identities, including source Mats.MAT_ALUMINIUM.id=1300 shell metadata. */
final class LegacyPileItems {
    static Item item(String name){return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("hbm:"+name)).orElseThrow(()->new IllegalStateException("Missing true legacy pile dependency hbm:"+name));}
    static ItemStack stack(String name,int count){return new ItemStack(item(name),count);}
    static ItemStack shell(){var stack=stack("shell",1);stack.set(HbmHazards.LEGACY_METADATA.get(),1300);return stack;}
    static boolean is(ItemStack stack,String name){return !stack.isEmpty()&&stack.getItem()==item(name);}
    static boolean shell(ItemStack stack){return is(stack,"shell")&&stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())==1300;}
    private LegacyPileItems(){}
}
