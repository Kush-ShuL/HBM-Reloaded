package com.hbm.item.starcomponents;

import com.hbm.item.EnumMultiItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact ItemCircuitStarComponent only delegates to ItemEnumMulti(true,true); no source hand-use behavior exists. */
public final class StarComponentsContent {
    public static final List<String> NAMES = List.of("chipset", "cpu", "ram", "card");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<EnumMultiItem> ITEM = ITEMS.register("circuit_star_component", () -> new EnumMultiItem("circuit_star_component", 64, true, NAMES, List.of(0,1,2,3)));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return ITEM.get().creativeStacks(); }
    private StarComponentsContent() { }
}
