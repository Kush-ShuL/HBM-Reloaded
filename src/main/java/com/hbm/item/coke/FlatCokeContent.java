package com.hbm.item.coke;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatCokeContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("coal","lignite","petroleum");
    public static final List<DeferredItem<FlatCokeItem>> COKES=NAMES.stream().map(name->ITEMS.register("coke_"+name,FlatCokeItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(COKES.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?COKES.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatCokeContent(){}
}
