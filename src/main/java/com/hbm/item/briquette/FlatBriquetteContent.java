package com.hbm.item.briquette;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatBriquetteContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("coal","lignite","wood");
    public static final List<DeferredItem<FlatBriquetteItem>> BRIQUETTES=NAMES.stream().map(name->ITEMS.register("briquette_"+name,FlatBriquetteItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(BRIQUETTES.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?BRIQUETTES.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatBriquetteContent(){}
}
