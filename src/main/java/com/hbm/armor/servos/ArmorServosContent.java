package com.hbm.armor.servos;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Two actual source items; installation, NBT, ticking and attributes use the unique armor-mod backend. */
public final class ArmorServosContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModServos> SERVO_SET = ITEMS.register("servo_set", () -> new ItemModServos(false));
    public static final DeferredItem<ItemModServos> SERVO_SET_DESH = ITEMS.register("servo_set_desh", () -> new ItemModServos(true));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(SERVO_SET.get()), new ItemStack(SERVO_SET_DESH.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorServosContent() { }
}
