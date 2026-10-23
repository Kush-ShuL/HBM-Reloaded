package com.hbm.item.meteorunstable;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class MeteorUnstableContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<MetastableIngotItem> METASTABLE = ITEMS.register("ingot_u238m2", MetastableIngotItem::new);
    public static final DeferredItem<MysterySpadeItem> SPADE = ITEMS.register("mysteryshovel", MysterySpadeItem::new);
    public static final DeferredItem<StarmetalIngotItem> STARMETAL = ITEMS.register("ingot_starmetal", StarmetalIngotItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    private static ItemStack stack(Item item, int metadata) { var stack = new ItemStack(item); stack.set(HbmHazards.LEGACY_METADATA.get(), metadata); return stack; }
    public static ItemStack metastable(int metadata) { return stack(METASTABLE.get(), metadata); }
    public static List<ItemStack> metastableVariants() { return IntStream.range(0, 4).mapToObj(MeteorUnstableContent::metastable).toList(); }
    public static List<ItemStack> starmetalVariants() { return IntStream.range(0, 4).mapToObj(meta -> stack(STARMETAL.get(), meta)).toList(); }
    // Source ItemStarmetal.getSubItems calls super only. Fragments and spade have no creative tab.
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("parts") ? List.of(new ItemStack(STARMETAL.get())) : List.of(); }
    private MeteorUnstableContent() {}
}
