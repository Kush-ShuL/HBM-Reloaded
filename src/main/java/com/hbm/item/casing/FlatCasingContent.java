package com.hbm.item.casing;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Seven physical source items. No particle, magazine, probability or bag inventory implementation lives here. */
public final class FlatCasingContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("small","large","small_steel","large_steel","shotshell","buckshot","buckshot_advanced");
    public static final List<DeferredItem<FlatCasingItem>> CASINGS=IntStream.range(0,NAMES.size()).mapToObj(i->ITEMS.register("casing_"+NAMES.get(i),()->new FlatCasingItem(i))).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(CASINGS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?CASINGS.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatCasingContent(){}
}
