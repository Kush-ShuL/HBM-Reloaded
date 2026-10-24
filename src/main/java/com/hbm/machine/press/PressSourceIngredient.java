package com.hbm.machine.press;

import com.hbm.hazard.HbmHazards;
import com.hbm.pneumatic.SourcePatternItems;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.crafting.*;

/** Original ComparableStack/NBTStack matching,using the one existing source Item-identity bridge after flattening. */
public record PressSourceIngredient(ItemStack example,boolean wildcard,CompoundTag required) implements ICustomIngredient {
    public static final MapCodec<PressSourceIngredient> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(ItemStack.CODEC.fieldOf("stack").forGetter(PressSourceIngredient::example),Codec.BOOL.fieldOf("wildcard").forGetter(PressSourceIngredient::wildcard),CompoundTag.CODEC.fieldOf("nbt").forGetter(PressSourceIngredient::required)).apply(instance,PressSourceIngredient::new));
    @Override public boolean test(ItemStack stack){if(stack.isEmpty()||!(wildcard?SourcePatternItems.sameItem(stack,example):SourcePatternItems.sameExact(stack,example)))return false;var actual=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();for(String key:required.getAllKeys())if(!required.get(key).equals(actual.get(key)))return false;return true;}
    @Override public Stream<ItemStack> getItems(){Stream<ItemStack> values;
        if(!wildcard)values=Stream.of(example.copy());else if(example.getItem() instanceof com.hbm.item.autogen.AutogenItem item)values=item.creativeStacks().stream();else values=net.minecraft.core.registries.BuiltInRegistries.ITEM.stream().map(ItemStack::new).filter(stack->!stack.isEmpty()&&SourcePatternItems.sameItem(stack,example));
        return values.map(stack->{var value=stack.copyWithCount(1);if(!required.isEmpty())value.set(DataComponents.CUSTOM_DATA,CustomData.of(required.copy()));return value;});
    }
    @Override public boolean isSimple(){return false;}
    @Override public IngredientType<?> getType(){return com.hbm.machine.electricpress.ElectricPressContent.SOURCE_INGREDIENT.get();}
}
