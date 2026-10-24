package com.hbm.item.starcomponents;

import com.hbm.item.FixedEnumItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact ItemCircuitStarComponent only delegates to ItemEnumMulti(true,true); no source hand-use behavior exists. */
public final class StarComponentsContent {
    public static final List<String> NAMES = List.of("chipset", "cpu", "ram", "card");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<DeferredItem<FixedEnumItem>> ITEMS_BY_TYPE=registerItems();
    private static List<DeferredItem<FixedEnumItem>> registerItems(){var items=new java.util.ArrayList<DeferredItem<FixedEnumItem>>();for(int ordinal=0;ordinal<NAMES.size();ordinal++){int type=ordinal;String name=NAMES.get(ordinal);items.add(ITEMS.register("circuit_star_component_"+name,()->new FixedEnumItem("circuit_star_component",name,type,64,true)));}return List.copyOf(items);}
    public static FixedEnumItem item(int type){return ITEMS_BY_TYPE.get(type).get();}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return ITEMS_BY_TYPE.stream().map(item->new ItemStack(item.get())).toList(); }
    private StarComponentsContent() { }
}
