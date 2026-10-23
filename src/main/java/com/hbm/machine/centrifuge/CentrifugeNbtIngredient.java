package com.hbm.machine.centrifuge;

import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.crafting.*;

/** Legacy NBTStack compares required top-level tags for equality and ignores additional top-level tags. */
public record CentrifugeNbtIngredient(ItemStack example,CompoundTag required) implements ICustomIngredient {
    public static final MapCodec<CentrifugeNbtIngredient> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(ItemStack.CODEC.fieldOf("stack").forGetter(CentrifugeNbtIngredient::example),CompoundTag.CODEC.fieldOf("nbt").forGetter(CentrifugeNbtIngredient::required)).apply(instance,CentrifugeNbtIngredient::new));
    @Override public boolean test(ItemStack stack) {
        if(stack.isEmpty()||stack.getItem()!=example.getItem()) return false;
        int meta=example.getOrDefault(HbmHazards.LEGACY_METADATA.get(),example.getDamageValue());
        if(meta!=32767&&meta!=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())) return false;
        CompoundTag actual=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
        for(String key:required.getAllKeys()) if(!required.get(key).equals(actual.get(key))) return false;
        return true;
    }
    @Override public Stream<ItemStack> getItems() { ItemStack stack=example.copy(); if(!required.isEmpty()) stack.set(DataComponents.CUSTOM_DATA,CustomData.of(required.copy())); return Stream.of(stack); }
    @Override public boolean isSimple() { return false; }
    @Override public IngredientType<?> getType() { return CentrifugeContent.NBT_INGREDIENT.get(); }
    @Override public boolean equals(Object other) { return other instanceof CentrifugeNbtIngredient value&&example.getItem()==value.example.getItem()&&metadata(example)==metadata(value.example)&&required.equals(value.required); }
    @Override public int hashCode() { return 31*(31*example.getItem().hashCode()+metadata(example))+required.hashCode(); }
    private static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()); }
}
