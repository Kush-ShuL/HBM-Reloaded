package com.hbm.item.secret;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class FlatSecretContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("canister","controller","selenium_steel","aberrator","folly");
    public static final List<DeferredItem<FlatSecretItem>> COMPONENTS=NAMES.stream().map(name->ITEMS.register("item_secret_"+name,FlatSecretItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    /** Source-build/internal ordinal only; no saved-stack or old-ID decoding. */
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(COMPONENTS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> allStacks(){return COMPONENTS.stream().map(item->new ItemStack(item.get())).toList();}
    private FlatSecretContent(){}
}
