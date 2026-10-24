package com.hbm.ore;

import com.google.gson.Gson;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.LegacyOreItem;
import com.hbm.block.ore.DeepslateOreItem;
import com.hbm.hazard.HbmHazards;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Modern mining identities, without changing the source processing or crafting ore dictionary. */
public final class RawOreContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final Content CONTENT = load();
    private static final Map<String, Source> BLOCK_SOURCES = new LinkedHashMap<>();
    private static final Map<String, Source> SOURCE_KEYS = new LinkedHashMap<>();
    private static final Map<String, Source> RAW_SOURCES = new LinkedHashMap<>();
    private static final List<DeferredItem<Item>> RAW_ITEMS = new ArrayList<>();

    static {
        for (Source source : CONTENT.mappings()) index(source, false);
        // Unmarked vanilla raw metals always retain their real vanilla canonical identity.
        for (Source source : CONTENT.vanillaCanonical()) index(source, true);
        // Every raw identity explicitly uses its one canonical mineral source,
        // independent of the order of host aliases in the generated catalog.
        for (Definition item : CONTENT.items()) {
            Source canonical = SOURCE_KEYS.get(key(item.sourceSymbol(), item.legacyMetadata()));
            if (canonical != null) RAW_SOURCES.put("hbm:" + item.id(), canonical);
        }
    }

    private RawOreContent() {}

    public static void register(IEventBus bus) {
        for (Definition item : CONTENT.items()) RAW_ITEMS.add(ITEMS.registerSimpleItem(item.id()));
        ITEMS.register(bus);
    }

    private static Content load() {
        try (var stream = RawOreContent.class.getResourceAsStream("/hbm/raw-ores.json")) {
            if (stream == null) throw new IllegalStateException("Missing raw ore definitions");
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
        } catch (java.io.IOException issue) {
            throw new IllegalStateException("Cannot load raw ore definitions", issue);
        }
    }

    private static void index(Source source, boolean canonical) {
        SOURCE_KEYS.put(source.key(), source);
        for (String block : source.blockItems()) BLOCK_SOURCES.put(key(block, com.hbm.block.ore.ResourceOreBlock.familyOf(block) != null ? 0 : source.legacyMetadata()), source);
        if (source.rawItem() != null) {
            if (canonical) RAW_SOURCES.put(source.rawItem(), source);
            else RAW_SOURCES.putIfAbsent(source.rawItem(), source);
        }
    }

    /** Raw -> canonical mineral recipe identity. Empty means this is not a managed raw material. */
    public static ItemStack originalStack(ItemStack raw) {
        Source source = rawSource(raw);
        if (source == null) return ItemStack.EMPTY;
        ItemStack original = raw.transmuteCopy(item(source.originalItem()));
        original.remove(HbmHazards.LEGACY_METADATA.get());
        if (original.getItem() instanceof LegacyOreItem ore && ore.spec().multi())
            original.set(HbmHazards.LEGACY_METADATA.get(), source.legacyMetadata());
        return original;
    }

    /** Processing-only normalization. Unrelated items and their legacy components are retained. */
    public static ItemStack processingStack(ItemStack input) {
        ItemStack original = originalStack(input);
        if (!original.isEmpty()) return original;
        return input.getItem() instanceof DeepslateOreItem deep ? deep.originalStack(input) : input.copy();
    }

    /** Original/deepslate recipe identity -> actual raw alternatives, retaining source and count. */
    public static List<ItemStack> rawAlternatives(ItemStack original) {
        Source source = oreSource(original);
        if (source == null || source.rawItem() == null) return List.of();
        ItemStack raw = rawStack(source, original.getCount());
        Source canonical = rawSource(raw);
        // Mining conversions from special hosts are not exact processing
        // equivalents: their plain raw uses the ordinary mineral's recipes.
        return canonical != null && canonical.originalItem().equals(source.originalItem())
                && canonical.legacyMetadata() == source.legacyMetadata() ? List.of(raw) : List.of();
    }

    public static boolean isRaw(ItemStack stack) {
        return !stack.isEmpty() && RAW_SOURCES.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
    }

    /** Includes actual vanilla Fe/Au/Cu hosts, for HBM processing; vanilla mining remains untouched. */
    public static boolean isManagedOre(ItemStack stack) { return oreSource(stack) != null; }

    public static Optional<Source> source(ItemStack stack) {
        Source source = rawSource(stack);
        return Optional.ofNullable(source != null ? source : oreSource(stack));
    }

    public static List<Source> sources() { return List.copyOf(SOURCE_KEYS.values()); }

    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("parts") ? RAW_ITEMS.stream().map(holder -> new ItemStack(holder.get())).toList() : List.of();
    }

    private static Source rawSource(ItemStack stack) {
        if (stack.isEmpty()) return null;
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return RAW_SOURCES.get(id);
    }

    private static Source oreSource(ItemStack stack) {
        if (stack.isEmpty()) return null;
        int metadata = stack.getItem() instanceof LegacyOreItem ore && ore.spec().multi()
                ? ore.ordinal(stack) : 0;
        return BLOCK_SOURCES.get(key(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), metadata));
    }

    private static ItemStack rawStack(Source source, int count) {
        return new ItemStack(item(source.rawItem()), count);
    }

    private static Item item(String id) {
        ResourceLocation key = ResourceLocation.parse(id);
        if (!BuiltInRegistries.ITEM.containsKey(key)) throw new IllegalStateException("Missing raw ore dependency " + id);
        return BuiltInRegistries.ITEM.get(key);
    }

    private static String key(String id, int metadata) { return id + "#" + metadata; }

    /**
     * Invoked only after the existing Silk Touch branch. Null leaves the source drop method
     * untouched; an empty list is a managed ore whose drops failed explosion survival.
     */
    public static List<ItemStack> minedDrops(BlockState state, LootParams.Builder context, ItemStack tool) {
        if (!(state.getBlock() instanceof LegacyOreBlock ore)) return null;
        int metadata = ore.spec().multi() ? ore.spec().ordinal(state.getValue(LegacyOreBlock.LEGACY_META)) : 0;
        Source source = BLOCK_SOURCES.get(key(BuiltInRegistries.ITEM.getKey(state.getBlock().asItem()).toString(), metadata));
        if (source == null || source.rawItem() == null && !source.sourceSymbol().equals("ore_meteor")) return null;
        MeteorDrop meteor = source.rawItem() == null ? CONTENT.meteorDrops().stream()
                .filter(drop -> drop.legacyMetadata() == source.legacyMetadata()).findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing meteor contained-material drop " + source.key())) : null;
        int baseCount = meteor == null ? 1 : meteor.count();
        RandomSource random = context.getLevel().random;
        int bonus = 0;
        // This gate precedes even the Fortune enchantment query and random bonus draw.
        if (OreProcessingConfig.APPLY_FORTUNE.get()) {
            int fortune = EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), tool);
            if (fortune > 0) bonus = Math.max(random.nextInt(fortune + 2) - 1, 0);
        }
        double extra = baseCount * bonus * OreProcessingConfig.FORTUNE_BONUS_MULTIPLIER.get();
        int count = baseCount + (int) extra;
        if (extra > Math.floor(extra) && random.nextDouble() < extra - Math.floor(extra)) count++;
        Float explosionRadius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        int surviving = 0;
        for (int i = 0; i < count; i++) {
            if (explosionRadius == null || random.nextFloat() <= 1F / explosionRadius) surviving++;
        }
        if (surviving == 0) return List.of();
        List<ItemStack> drops = new ArrayList<>();
        while (surviving > 0) {
            ItemStack drop = meteor == null ? rawStack(source, 1) : new ItemStack(item(meteor.item()));
            int amount = Math.min(surviving, drop.getMaxStackSize());
            drop.setCount(amount);
            drops.add(drop);
            surviving -= amount;
        }
        return drops;
    }

    /** Source symbol and original metadata remain independent from the modern host registry ID. */
    public record Source(String sourceSymbol, int legacyMetadata, String rawItem, String originalItem,
                         List<String> blockItems) {
        public Source { blockItems = List.copyOf(blockItems); }
        public String key() { return RawOreContent.key(sourceSymbol, legacyMetadata); }
    }
    private record Definition(String id, String sourceSymbol, int legacyMetadata) {}
    private record MeteorDrop(int legacyMetadata, String item, int count) {}
    private record Content(List<Definition> items, List<Source> mappings, List<Source> vanillaCanonical, List<MeteorDrop> meteorDrops) {}
}
