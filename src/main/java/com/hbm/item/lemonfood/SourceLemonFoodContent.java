package com.hbm.item.lemonfood;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the eleven genuinely absent ItemLemon members; existing ten owners retain their registrations. */
public final class SourceLemonFoodContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<SourceLemonFoodKind,DeferredItem<SourceLemonFoodItem>> FOODS=new LinkedHashMap<>();
    static {for(var kind:SourceLemonFoodKind.values())FOODS.put(kind,ITEMS.register(kind.name,()->new SourceLemonFoodItem(kind)));}
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?FOODS.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private SourceLemonFoodContent(){}
}
