package com.hbm.item.dye;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 两组真实颜色身份，创造栏仍先染料后蜡笔，各自按固定颜色顺序。 */
public final class ChemicalDyeContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final Map<ChemicalDyeType, DeferredItem<ChemicalDyeItem>> DYES = new EnumMap<>(ChemicalDyeType.class);
    private static final Map<ChemicalDyeType, DeferredItem<CrayonItem>> CRAYONS = new EnumMap<>(ChemicalDyeType.class);

    static {
        for (var type : ChemicalDyeType.values()) {
            DYES.put(type, ITEMS.register("chemical_dye_" + type.serializedName(), () -> new ChemicalDyeItem(type)));
        }
        for (var type : ChemicalDyeType.values()) {
            CRAYONS.put(type, ITEMS.register("crayon_" + type.serializedName(), () -> new CrayonItem(type)));
        }
    }

    private ChemicalDyeContent() { }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm", name); }
    public static ChemicalDyeItem dye(ChemicalDyeType type) { return DYES.get(type).get(); }
    public static CrayonItem crayon(ChemicalDyeType type) { return CRAYONS.get(type).get(); }

    public static List<Item> items() {
        var result = new ArrayList<Item>(32);
        for (var type : ChemicalDyeType.values()) result.add(dye(type));
        for (var type : ChemicalDyeType.values()) result.add(crayon(type));
        return result;
    }

    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("parts") ? items().stream().map(ItemStack::new).toList() : List.of();
    }
}
