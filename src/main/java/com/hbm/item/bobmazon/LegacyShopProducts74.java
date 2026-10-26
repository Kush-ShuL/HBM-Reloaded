package com.hbm.item.bobmazon;

import com.hbm.bomb.nuclear.NuclearStarterKit;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Original starter-kit branches and actual frequency chips used by the shop. */
public final class LegacyShopProducts74 {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<java.util.function.Supplier<? extends net.minecraft.world.item.Item>> KITS = new ArrayList<>();
    // Hazmat kits are owned by SurvivalLootContent; shop offers resolve those existing item IDs.
    public static final String[] KIT_NAMES = {"nuke_starter_kit", "nuke_advanced_kit", "nuke_commercially_kit", "nuke_electric_kit", "missile_kit", "euphemium_kit"};
    static {
        for (String symbol : KIT_NAMES) KITS.add(ITEMS.register(symbol, () -> new NuclearStarterKit(symbol)));
    }
    public static final net.neoforged.neoforge.registries.DeferredItem<LaserFrequencyChip> LASER =
            ITEMS.register("sat_laser", LaserFrequencyChip::new);
    public static final net.neoforged.neoforge.registries.DeferredItem<GeraldFrequencyChip> GERALD =
            ITEMS.register("sat_gerald", GeraldFrequencyChip::new);

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> allStacks() {
        var result = new ArrayList<ItemStack>();
        for (var item : KITS) result.add(new ItemStack(item.get()));
        result.add(new ItemStack(LASER.get())); result.add(new ItemStack(GERALD.get()));
        return result;
    }
    public static List<ItemStack> creativeStacks(String tab) {
        var result = new ArrayList<ItemStack>();
        if (tab.equals("consumable")) for (int i = 0; i < KIT_NAMES.length; i++) {
            if (!KIT_NAMES[i].equals("missile_kit") && !KIT_NAMES[i].equals("euphemium_kit")) result.add(new ItemStack(KITS.get(i).get()));
        }
        if (tab.equals("missile")) {
            result.add(new ItemStack(kit("missile_kit"))); result.add(new ItemStack(GERALD.get()));
        }
        // Source sat_laser has no creative tab.
        return result;
    }
    private static net.minecraft.world.item.Item kit(String symbol) {
        for (int i = 0; i < KIT_NAMES.length; i++) if (KIT_NAMES[i].equals(symbol)) return KITS.get(i).get();
        throw new IllegalArgumentException("Unknown original starter kit " + symbol);
    }
    private LegacyShopProducts74() {}
}
