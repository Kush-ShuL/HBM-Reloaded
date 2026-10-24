package com.hbm.item.battery;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BatteryContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final java.util.Map<BatteryPackItem.EnumBatteryPack,DeferredItem<BatteryPackItem>> PACK = registerPacks();
    private static java.util.Map<BatteryPackItem.EnumBatteryPack,DeferredItem<BatteryPackItem>> registerPacks() {var result=new java.util.EnumMap<BatteryPackItem.EnumBatteryPack,DeferredItem<BatteryPackItem>>(BatteryPackItem.EnumBatteryPack.class);for(var type:BatteryPackItem.EnumBatteryPack.values())result.put(type,ITEMS.register("battery_pack_"+type.name().toLowerCase(java.util.Locale.ROOT),()->new BatteryPackItem(type)));return java.util.Collections.unmodifiableMap(result);}
    public static ItemStack pack(BatteryPackItem.EnumBatteryPack type) {return new ItemStack(PACK.get(type).get());}
    public static List<BatteryPackItem> packItems() {return java.util.Arrays.stream(BatteryPackItem.EnumBatteryPack.values()).map(type->PACK.get(type).get()).toList();}
    public static List<ItemStack> packStacks() {return java.util.Arrays.stream(BatteryPackItem.EnumBatteryPack.values()).map(BatteryContent::pack).toList();}
    public static List<ItemStack> packCreativeStacks() {return packItems().stream().flatMap(item->item.creativeStacks().stream()).toList();}
    public static final java.util.Map<SelfChargingBatteryItem.EnumBatterySC,DeferredItem<SelfChargingBatteryItem>> SELF_CHARGING = registerSelfCharging();
    private static java.util.Map<SelfChargingBatteryItem.EnumBatterySC,DeferredItem<SelfChargingBatteryItem>> registerSelfCharging() {var result=new java.util.EnumMap<SelfChargingBatteryItem.EnumBatterySC,DeferredItem<SelfChargingBatteryItem>>(SelfChargingBatteryItem.EnumBatterySC.class);for(var type:SelfChargingBatteryItem.EnumBatterySC.values())result.put(type,ITEMS.register("battery_sc_"+type.name().toLowerCase(java.util.Locale.ROOT),()->new SelfChargingBatteryItem(type)));return java.util.Collections.unmodifiableMap(result);}
    public static ItemStack selfCharging(SelfChargingBatteryItem.EnumBatterySC type) {return new ItemStack(SELF_CHARGING.get(type).get());}
    public static List<ItemStack> selfChargingStacks() {return java.util.Arrays.stream(SelfChargingBatteryItem.EnumBatterySC.values()).map(BatteryContent::selfCharging).toList();}
    public static final DeferredItem<BatteryItem> CUBE_POWER = ITEMS.register("cube_power", () -> new BatteryItem(1_000_000_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000_000L));
    public static final DeferredItem<BatteryItem> POTATO = ITEMS.register("battery_potato", () -> new BatteryItem(1_000L, 0L, 100L));
    public static final DeferredItem<BatteryItem> MEMORY = ITEMS.register("memory", () -> new BatteryItem(Long.MAX_VALUE / 100L, 100_000_000_000_000L, 100_000_000_000_000L));
    public static final DeferredItem<CreativeBatteryItem> CREATIVE = ITEMS.register("battery_creative", CreativeBatteryItem::new);
    private BatteryContent() {}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> controlItems() {
        List<ItemStack> result = new ArrayList<>();
        result.addAll(packCreativeStacks());
        result.add(new ItemStack(CREATIVE.get()));
        result.add(CUBE_POWER.get().emptyStack()); result.add(CUBE_POWER.get().fullStack());
        result.addAll(selfChargingStacks());
        result.add(POTATO.get().fullStack());
        return result;
    }
}
