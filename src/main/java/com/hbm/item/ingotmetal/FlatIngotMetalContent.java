package com.hbm.item.ingotmetal;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatIngotMetalContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("scrap","ingot","counter","key","beacon","casing","clockwork","bar","detector");
    public static final List<DeferredItem<FlatIngotMetalItem>> COMPONENTS=NAMES.stream().map(name->ITEMS.register("ingot_metal_"+name,FlatIngotMetalItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(COMPONENTS.get(sourceOrdinal).get(),count);}
    /** Source order for the NEIConfig hide rule; intentionally no creative-tab entry. */
    public static List<ItemStack> allStacks(){return COMPONENTS.stream().map(item->new ItemStack(item.get())).toList();}
    private FlatIngotMetalContent(){}
}
