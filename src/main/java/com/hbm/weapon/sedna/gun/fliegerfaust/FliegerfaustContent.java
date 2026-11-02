package com.hbm.weapon.sedna.gun.fliegerfaust;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FliegerfaustContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> FLIEGERFAUST = ITEMS.register("gun_fliegerfaust", FliegerfaustFactory::create);

    private FliegerfaustContent() { }

    public static void register(IEventBus bus, ModContainer container) {
        FliegerfaustSettings.register(container);
        ITEMS.register(bus);
    }

    public static List<ItemStack> allStacks() { return List.of(new ItemStack(FLIEGERFAUST.get())); }
    public static List<ItemStack> indexedItems() { return allStacks(); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("weapon") ? allStacks() : List.of(); }
}
