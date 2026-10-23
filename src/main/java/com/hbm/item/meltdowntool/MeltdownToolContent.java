package com.hbm.item.meltdowntool;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class MeltdownToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<MeltdownToolItem> ITEM=ITEMS.register("meltdown_tool",MeltdownToolItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "control".equals(tab)?indexedItems():List.of();}
    private MeltdownToolContent(){}
}
