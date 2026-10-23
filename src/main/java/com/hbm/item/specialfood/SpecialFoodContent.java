package com.hbm.item.specialfood;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the three absent source foods; glowing_stew remains owned by the existing RBMK outgasser. */
public final class SpecialFoodContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceSoupItem> BALEFIRE_SCRAMBLED =
            ITEMS.register("balefire_scrambled", () -> new SourceSoupItem("balefire_scrambled"));
    public static final DeferredItem<SourceSoupItem> BALEFIRE_AND_HAM =
            ITEMS.register("balefire_and_ham", () -> new SourceSoupItem("balefire_and_ham"));
    public static final DeferredItem<BombWaffleItem> BOMB_WAFFLE = ITEMS.register("bomb_waffle", BombWaffleItem::new);

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("consumable") ? List.of(new ItemStack(BALEFIRE_SCRAMBLED.get()),
                new ItemStack(BALEFIRE_AND_HAM.get()), new ItemStack(BOMB_WAFFLE.get())) : List.of();
    }
    private SpecialFoodContent() {}
}
