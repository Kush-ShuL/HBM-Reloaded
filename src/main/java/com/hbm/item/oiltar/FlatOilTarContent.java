package com.hbm.item.oiltar;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatOilTarContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("crude","crack","coal","wood","wax","paraffin");
    public static final List<DeferredItem<FlatOilTarItem>> TARS=NAMES.stream().map(name->ITEMS.register("oil_tar_"+name,FlatOilTarItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(TARS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?TARS.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatOilTarContent(){}
}
