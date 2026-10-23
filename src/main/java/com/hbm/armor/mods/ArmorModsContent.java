package com.hbm.armor.mods;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Four true armor modifications, sharing the existing table,9-slot storage and powered suit engine. */
public final class ArmorModsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModTesla> BACK_TESLA = ITEMS.register("back_tesla", ItemModTesla::new);
    public static final DeferredItem<ItemModLodestone> LODESTONE = ITEMS.register("lodestone", () -> new ItemModLodestone("lodestone", 5));
    public static final DeferredItem<ItemModLodestone> HORSESHOE = ITEMS.register("horseshoe_magnet", () -> new ItemModLodestone("horseshoe_magnet", 8));
    public static final DeferredItem<ItemModLodestone> INDUSTRIAL = ITEMS.register("industrial_magnet", () -> new ItemModLodestone("industrial_magnet", 12));
    public static void register(IEventBus bus) {
        ITEMS.register(bus); ArmorModRuntime.register(); MagnetNetwork.register(bus);
        NeoForge.EVENT_BUS.addListener(ArmorModsContent::smelted);
    }
    private static void smelted(PlayerEvent.ItemSmeltedEvent event) {
        var player = event.getEntity();
        // Source one draw per take-output event, irrespective of the iron stack count.
        if (!player.level().isClientSide && event.getSmelting().is(Items.IRON_INGOT) && player.getRandom().nextInt(64) == 0) {
            var lodestone = new ItemStack(LODESTONE.get());
            if (!player.getInventory().add(lodestone)) player.drop(lodestone, false);
            else player.containerMenu.broadcastChanges();
        }
    }
    public static List<ItemStack> indexedItems() {
        return List.of(new ItemStack(BACK_TESLA.get()), new ItemStack(LODESTONE.get()), new ItemStack(HORSESHOE.get()), new ItemStack(INDUSTRIAL.get()));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorModsContent() { }
}
