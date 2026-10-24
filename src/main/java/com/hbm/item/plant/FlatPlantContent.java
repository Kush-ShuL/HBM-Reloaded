package com.hbm.item.plant;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Registered and grouped exactly once through the real OilBotanyContent lifecycle. */
public final class FlatPlantContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("tobacco","rope","mustardwillow");
    public static final List<DeferredItem<FlatPlantItem>> MATERIALS=NAMES.stream().map(name->ITEMS.register("plant_item_"+name,FlatPlantItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    /** Strict internal source ordinal; no old stack or saved-ID decoder. */
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(MATERIALS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> allStacks(){return MATERIALS.stream().map(item->new ItemStack(item.get())).toList();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?allStacks():List.of();}
    private FlatPlantContent(){}
}
