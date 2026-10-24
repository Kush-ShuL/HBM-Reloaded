package com.hbm.item.page;
import java.util.List;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Source tab=null: no creative entry and no inferred recipe-viewer secret policy. */
public final class FlatPageContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("page1","page2","page3","page4","page5","page6","page7","page8");
    public static final List<DeferredItem<FlatPageItem>> PAGES=NAMES.stream().map(name->ITEMS.register("page_of_"+name,FlatPageItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    private FlatPageContent(){}
}
