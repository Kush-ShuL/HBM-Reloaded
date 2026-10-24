package com.hbm.item.marshmallow;

import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MarshmallowContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemMarshmallow> MARSHMALLOW = ITEMS.register("marshmallow", ItemMarshmallow::new);
    public static final DeferredItem<ItemMarshmallow> ROASTED = ITEMS.register("marshmallow_roasted", ItemMarshmallow::new);
    public static final DeferredItem<StructureFoodItem> SMORE = ITEMS.register("ingot_smore", () -> new StructureFoodItem("ingot_smore", 10, 20F));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(MARSHMALLOW.get()), new ItemStack(ROASTED.get()), new ItemStack(SMORE.get())); }
    public static List<ItemStack> creativeStacks(String tab) {
        if ("consumable".equals(tab)) return List.of(new ItemStack(MARSHMALLOW.get()), new ItemStack(ROASTED.get()));
        if ("parts".equals(tab)) return List.of(new ItemStack(SMORE.get()));
        return List.of();
    }
    private MarshmallowContent() { }
}
