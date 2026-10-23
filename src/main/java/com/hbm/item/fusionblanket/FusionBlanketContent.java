package com.hbm.item.fusionblanket;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FusionBlanketContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemFusionShield> TUNGSTEN = ITEMS.register("fusion_shield_tungsten",
        () -> new ItemFusionShield(60 * 60 * 60 * 5, 3500));
    public static final DeferredItem<ItemFusionShield> DESH = ITEMS.register("fusion_shield_desh",
        () -> new ItemFusionShield(60 * 60 * 60 * 10, 4500));
    public static final DeferredItem<ItemFusionShield> CHLOROPHYTE = ITEMS.register("fusion_shield_chlorophyte",
        () -> new ItemFusionShield(60 * 60 * 60 * 15, 9000));
    public static final DeferredItem<ItemFusionShield> VAPORWAVE = ITEMS.register("fusion_shield_vaporwave",
        () -> new ItemFusionShield(60 * 60 * 60 * 10, 1916169));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() {
        return List.of(new ItemStack(TUNGSTEN.get()), new ItemStack(DESH.get()),
            new ItemStack(CHLOROPHYTE.get()), new ItemStack(VAPORWAVE.get()));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "control".equals(tab) ? indexedItems() : List.of(); }
    private FusionBlanketContent() { }
}
