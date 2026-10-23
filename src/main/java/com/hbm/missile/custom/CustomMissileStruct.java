package com.hbm.missile.custom;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import static com.hbm.missile.custom.CustomMissileParts.*;

/** The sole original four-part MissileStruct; optional fins remain null. Registry names replace unstable numeric IDs. */
public record CustomMissileStruct(CustomMissilePartItem warhead, CustomMissilePartItem fuselage,
                                  CustomMissilePartItem fins, CustomMissilePartItem thruster) {
    public CustomMissileStruct(ItemStack warhead, ItemStack fuselage, ItemStack fins, ItemStack thruster) {
        this(part(warhead), part(fuselage), part(fins), part(thruster));
    }
    private static CustomMissilePartItem part(ItemStack stack) { return stack.getItem() instanceof CustomMissilePartItem part ? part : null; }
    public CompoundTag write() {
        var tag = new CompoundTag(); put(tag, "warhead", warhead, PartType.WARHEAD); put(tag, "fuselage", fuselage, PartType.FUSELAGE);
        put(tag, "fins", fins, PartType.FINS); put(tag, "thruster", thruster, PartType.THRUSTER); return tag;
    }
    private static void put(CompoundTag tag, String key, CustomMissilePartItem item, PartType type) {
        if (item != null && item.definition.type() == type) tag.putString(key, BuiltInRegistries.ITEM.getKey(item).toString());
    }
    public static CustomMissileStruct read(CompoundTag tag) { return new CustomMissileStruct(readPart(tag, "warhead"), readPart(tag, "fuselage"), readPart(tag, "fins"), readPart(tag, "thruster")); }
    public static CustomMissilePartItem readPart(CompoundTag tag, String key) {
        var id = ResourceLocation.tryParse(tag.getString(key));
        if (id == null) return null;
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        return item instanceof CustomMissilePartItem part ? part : null;
    }
}
