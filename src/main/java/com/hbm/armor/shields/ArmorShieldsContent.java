package com.hbm.armor.shields;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact source ItemModInsert constructor arguments, then the one genuine ItemModShield. */
public final class ArmorShieldsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final Map<String, DeferredItem<ItemModInsert>> INSERTS = new LinkedHashMap<>();
    public static final DeferredItem<ItemModInsert> KEVLAR = insert("insert_kevlar", 1500, 1F, .9F, 1F, 1F);
    public static final DeferredItem<ItemModInsert> SAPI = insert("insert_sapi", 1750, 1F, .85F, 1F, 1F);
    public static final DeferredItem<ItemModInsert> ESAPI = insert("insert_esapi", 2000, .95F, .8F, 1F, 1F);
    public static final DeferredItem<ItemModInsert> XSAPI = insert("insert_xsapi", 2500, .9F, .75F, 1F, 1F);
    public static final DeferredItem<ItemModInsert> STEEL = insert("insert_steel", 1000, 1F, .95F, .75F, .95F);
    public static final DeferredItem<ItemModInsert> DU = insert("insert_du", 1500, .9F, .85F, .5F, .9F);
    public static final DeferredItem<ItemModInsert> POLONIUM = insert("insert_polonium", 500, .9F, 1F, .95F, .9F);
    public static final DeferredItem<ItemModInsert> GHIORSIUM = insert("insert_ghiorsium", 2000, .8F, .75F, .35F, .9F);
    public static final DeferredItem<ItemModInsert> ERA = insert("insert_era", 25, .5F, 1F, .25F, 1F);
    public static final DeferredItem<ItemModInsert> YHARONITE = insert("insert_yharonite", 9999, .01F, 1F, 1F, 1F);
    public static final DeferredItem<ItemModInsert> DOXIUM = insert("insert_doxium", 9999, 5F, 1F, 1F, 1F);
    public static final DeferredItem<ItemModShield> AUSTRALIUM_III = ITEMS.register("australium_iii", () -> new ItemModShield(25F));
    private static DeferredItem<ItemModInsert> insert(String name, int durability, float damage, float projectile, float explosion, float speed) {
        var result = ITEMS.register(name, () -> new ItemModInsert(name, durability, damage, projectile, explosion, speed));
        INSERTS.put(name, result); return result;
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() {
        var result = new ArrayList<ItemStack>();
        for (var item : INSERTS.values()) result.add(new ItemStack(item.get()));
        result.add(new ItemStack(AUSTRALIUM_III.get())); return List.copyOf(result);
    }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorShieldsContent() { }
}
