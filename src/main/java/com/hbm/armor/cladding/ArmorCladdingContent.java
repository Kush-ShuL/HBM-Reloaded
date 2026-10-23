package com.hbm.armor.cladding;

import com.hbm.armor.ArmorModStorage;
import java.util.List;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArmorCladdingContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModIron> IRON = ITEMS.register("cladding_iron", ItemModIron::new);
    public static final DeferredItem<ItemModObsidian> OBSIDIAN = ITEMS.register("cladding_obsidian", ItemModObsidian::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); NeoForge.EVENT_BUS.addListener(ArmorCladdingContent::toss); }
    private static void toss(ItemTossEvent event) {
        var dropped = event.getEntity(); var armor = dropped.getItem();
        if (!(armor.getItem() instanceof ArmorItem) || !ArmorModStorage.hasMods(armor)) return;
        var cladding = ArmorModStorage.pryMod(armor, ArmorModStorage.CLADDING, dropped.registryAccess());
        if (cladding.is(OBSIDIAN.get())) dropped.setInvulnerable(true);
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(IRON.get()), new ItemStack(OBSIDIAN.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorCladdingContent() { }
}
