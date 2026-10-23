package com.hbm.item.powernettool;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class PowerNetToolContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<PowerNetToolItem> ITEM = ITEMS.register("power_net_tool", PowerNetToolItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(PowerNetToolNotice::register); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(ITEM.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private PowerNetToolContent() { }
}
