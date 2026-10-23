package com.hbm.item.ash;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatAshContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("wood","coal","misc","fly","soot","fullerene");
    public static final List<DeferredItem<FlatAshItem>> ASHES=NAMES.stream().map(name->ITEMS.register("powder_ash_"+name,FlatAshItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(ASHES.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?ASHES.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatAshContent(){}
}
