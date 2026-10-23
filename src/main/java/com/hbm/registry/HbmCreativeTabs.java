package com.hbm.registry;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import com.hbm.hazard.HbmHazards;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source order is independent of which rewritten subsystem supplies an item. */
public final class HbmCreativeTabs {
    public static final List<String> ORDER = List.of("parts", "control", "template", "block", "machine", "nuke", "missile", "weapon", "consumable");
    private static final DeferredRegister.Items UI_ITEMS = DeferredRegister.createItems("hbm");
    /** UI artwork carrier only; absent from content tabs, recipes and source migration counts. */
    public static final DeferredItem<Item> UI_ICON = UI_ITEMS.register("ui/creative_tab_icon", () -> new Item(new Item.Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)));
    private static final Map<ResourceLocation, Integer> ITEM_ORDER = new HashMap<>();
    private static final Spec SPEC;

    static {
        try (var stream = HbmCreativeTabs.class.getResourceAsStream("/hbm/creative-tabs.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_creative_tabs.py");
            SPEC = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Spec.class);
            int ordinal = 0;
            for (String id : SPEC.registrationOrder()) ITEM_ORDER.putIfAbsent(ResourceLocation.parse(id), ordinal++);
        } catch (java.io.IOException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    private HbmCreativeTabs() {}

    public static void register(IEventBus bus) { UI_ITEMS.register(bus); }

    public static Map<String, IconSpec> iconModels() { return SPEC.iconModels(); }

    public static ItemStack uiIcon(String tab) {
        IconSpec spec = SPEC.iconModels().get(tab);
        if (spec == null) throw new IllegalArgumentException("Unknown original HBM tab " + tab);
        ItemStack stack = new ItemStack(UI_ICON.get());
        stack.set(HbmHazards.LEGACY_METADATA.get(), spec.index());
        return stack;
    }

    public static ItemStack icon(String tab) {
        String preferred = SPEC.icons().get(tab);
        if (preferred != null) {
            ResourceLocation id = ResourceLocation.parse(preferred);
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id));
                int metadata = SPEC.iconModels().get(tab).metadata();
                if (stack.isDamageableItem()) stack.setDamageValue(metadata);
                else if (metadata != 0 || stack.has(HbmHazards.LEGACY_METADATA.get())) stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
                return stack;
            }
        }
        return uiIcon(tab);
    }

    public static List<ItemStack> vanillaReplacements(String tab) {
        return SPEC.vanillaReplacements().stream().filter(row -> row.tab().equals(tab))
                .map(row -> new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(row.id())))).toList();
    }

    public static void sort(List<ItemStack> stacks) {
        stacks.sort(Comparator.comparingInt(stack -> ITEM_ORDER.getOrDefault(BuiltInRegistries.ITEM.getKey(stack.getItem()), Integer.MAX_VALUE)));
    }

    private record Spec(List<String> registrationOrder, Map<String, String> icons, Map<String, IconSpec> iconModels, List<Replacement> vanillaReplacements) {}
    public record IconSpec(int index, String item, int metadata, String renderer, String model, String texture) {}
    private record Replacement(String id, String tab) {}
}
