package com.hbm.item.pipette;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The three genuine control-tab pipettes; fluid/energy backends are not duplicated. */
public final class PipetteContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredItem<PipetteItem>> PIPETTES=new LinkedHashMap<>();
    static {for(var kind:PipetteItem.Kind.values())PIPETTES.put(kind.sourceName,ITEMS.register(kind.sourceName,()->new PipetteItem(kind)));}
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?PIPETTES.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private PipetteContent(){}
}
