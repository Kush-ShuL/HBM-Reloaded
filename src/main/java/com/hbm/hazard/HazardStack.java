package com.hbm.hazard;

import java.util.List;
import java.util.OptionalDouble;
import com.hbm.registry.HbmOreDictionary;
import com.hbm.block.ore.DeepslateOreItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** Stack identity and source-ordered tags, with optional actual component access. */
public record HazardStack(String item, int metadata, int count,
                          List<String> orderedTags, OptionalDouble extraRadiation,
                          ItemStack original) {
    public HazardStack {
        orderedTags = List.copyOf(orderedTags);
    }

    public static HazardStack from(ItemStack stack) {
        ItemStack identity = stack.getItem() instanceof DeepslateOreItem deep ? deep.originalStack(stack) : stack;
        ItemStack rawSource = com.hbm.ore.RawOreContent.originalStack(stack);
        if (!rawSource.isEmpty()) identity = rawSource;
        var extra = OptionalDouble.empty();
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom != null) {
            var tag = custom.copyTag();
            if (tag.contains("hfrHazRadiation")) extra = OptionalDouble.of(tag.getFloat("hfrHazRadiation"));
        }
        return new HazardStack(BuiltInRegistries.ITEM.getKey(identity.getItem()).toString(),
                identity.getOrDefault(HbmHazards.LEGACY_METADATA.get(), identity.getDamageValue()), stack.getCount(),
                HbmOreDictionary.tags(identity), extra, stack);
    }
}
