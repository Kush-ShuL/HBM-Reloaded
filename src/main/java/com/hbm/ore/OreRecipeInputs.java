package com.hbm.ore;

import com.hbm.hazard.HbmHazards;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Explicit machine extraction inputs; this is deliberately not a global crafting ingredient. */
public final class OreRecipeInputs {
    private OreRecipeInputs() {}

    public static boolean test(Ingredient ingredient, ItemStack input) {
        if (input.isEmpty()) return false;
        // A provenance-marked vanilla raw must test its actual host, not lose its identity
        // through the bare vanilla item's ordinary ingredient alternatives.
        if (RawOreContent.isRaw(input)) return ingredient.test(RawOreContent.originalStack(input));
        return ingredient.test(input) || ingredient.test(RawOreContent.processingStack(input));
    }

    public static boolean sameLegacy(ItemStack expected, int expectedMetadata, ItemStack input) {
        if (input.isEmpty()) return false;
        ItemStack actual = RawOreContent.processingStack(input);
        return same(expected, expectedMetadata, actual)
                || !RawOreContent.isRaw(input) && same(expected, expectedMetadata, input);
    }

    private static boolean same(ItemStack expected, int metadata, ItemStack actual) {
        return ItemStack.isSameItem(expected, actual) && (metadata == 32767
                || actual.getOrDefault(HbmHazards.LEGACY_METADATA.get(), actual.getDamageValue()) == metadata);
    }

    /** Source, genuine modern hosts and raw alternatives, including exact source provenance. */
    public static List<ItemStack> examples(Ingredient ingredient) { return examples(ingredient, 1); }

    public static List<ItemStack> examples(Ingredient ingredient, int count) {
        List<ItemStack> result = new ArrayList<>();
        Arrays.stream(ingredient.getItems()).forEach(stack -> result.add(stack.copyWithCount(Math.max(1, count))));
        for (var source : RawOreContent.sources()) {
            ItemStack original = original(source, Math.max(1, count));
            if (ingredient.test(original)) appendSource(result, source, original);
        }
        return distinct(result);
    }

    /** A ComparableStack recipe carries its legacy metadata separately from the input stack. */
    public static List<ItemStack> examples(ItemStack expected, int metadata) {
        List<ItemStack> result = new ArrayList<>();
        ItemStack display = expected.copy();
        if (metadata != 32767 && (metadata != 0 || display.has(HbmHazards.LEGACY_METADATA.get())))
            display.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        result.add(display);
        for (var source : RawOreContent.sources()) {
            ItemStack original = original(source, expected.getCount());
            if (same(expected, metadata, original)) appendSource(result, source, original);
        }
        return distinct(result);
    }

    public static List<ItemStack> examples(List<ItemStack> originals) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack original : originals) {
            int metadata = original.getOrDefault(HbmHazards.LEGACY_METADATA.get(), original.getDamageValue());
            result.addAll(examples(original, metadata));
        }
        return distinct(result);
    }

    public static List<ItemStack> examples(RawOreContent.Source source, int count) {
        List<ItemStack> result = new ArrayList<>();
        appendSource(result, source, original(source, count));
        return distinct(result);
    }

    private static ItemStack original(RawOreContent.Source source, int count) {
        ItemStack stack = new ItemStack(item(source.originalItem()), count);
        if (source.legacyMetadata() != 0 || stack.getItem() instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
            stack.set(HbmHazards.LEGACY_METADATA.get(), source.legacyMetadata());
        return stack;
    }

    private static void appendSource(List<ItemStack> result, RawOreContent.Source source, ItemStack original) {
        result.add(original);
        for (String id : source.blockItems()) result.add(original.transmuteCopy(item(id)));
        result.addAll(RawOreContent.rawAlternatives(original));
    }

    private static Item item(String id) { return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)); }

    private static List<ItemStack> distinct(List<ItemStack> values) {
        var unique = new LinkedHashMap<StackKey, ItemStack>();
        for (ItemStack stack : values) if (!stack.isEmpty())
            unique.putIfAbsent(new StackKey(stack.getItem(), stack.getComponentsPatch(), stack.getCount()), stack);
        return List.copyOf(unique.values());
    }

    /** The viewer calls this at hover time, so server-synchronized configuration is current. */
    public static List<Component> tooltip(ItemStack shown) {
        if (!RawOreContent.isRaw(shown) && !RawOreContent.isManagedOre(shown)) return List.of();
        boolean raw = RawOreContent.isRaw(shown);
        int percent = !OreProcessingConfig.SPEC.isLoaded() ? 100
                : raw ? OreProcessingConfig.RAW_YIELD_PERCENT.get() : OreProcessingConfig.BLOCK_YIELD_PERCENT.get();
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.hbm.raw_ore.processing_yield", percent).withStyle(ChatFormatting.GRAY));
        if (percent % 100 != 0)
            lines.add(Component.translatable("gui.hbm.raw_ore.fractional_output").withStyle(ChatFormatting.GRAY));
        return List.copyOf(lines);
    }

    private record StackKey(Item item, DataComponentPatch components, int count) {}
}
