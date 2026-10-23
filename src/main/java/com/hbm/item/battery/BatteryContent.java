package com.hbm.item.battery;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BatteryContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<BatteryPackItem> PACK = ITEMS.register("battery_pack", BatteryPackItem::new);
    public static final DeferredItem<SelfChargingBatteryItem> SELF_CHARGING = ITEMS.register("battery_sc", SelfChargingBatteryItem::new);
    public static final DeferredItem<BatteryItem> CUBE_POWER = ITEMS.register("cube_power", () -> new BatteryItem(1_000_000_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000_000L));
    public static final DeferredItem<BatteryItem> POTATO = ITEMS.register("battery_potato", () -> new BatteryItem(1_000L, 0L, 100L));
    public static final DeferredItem<BatteryItem> MEMORY = ITEMS.register("memory", () -> new BatteryItem(Long.MAX_VALUE / 100L, 100_000_000_000_000L, 100_000_000_000_000L));
    public static final DeferredItem<CreativeBatteryItem> CREATIVE = ITEMS.register("battery_creative", CreativeBatteryItem::new);
    private BatteryContent() {}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<com.hbm.item.EnumMultiItem> enumItems() { return List.of(PACK.get(), SELF_CHARGING.get()); }
    public static List<ItemStack> controlItems() {
        List<ItemStack> result = new ArrayList<>();
        result.addAll(PACK.get().creativeStacks());
        result.add(new ItemStack(CREATIVE.get()));
        result.add(CUBE_POWER.get().emptyStack()); result.add(CUBE_POWER.get().fullStack());
        result.addAll(SELF_CHARGING.get().creativeStacks());
        result.add(POTATO.get().fullStack());
        return result;
    }
}
