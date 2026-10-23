package com.hbm.item.designator;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class DesignatorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<TargetDesignatorItem> DESIGNATOR=ITEMS.register("designator",TargetDesignatorItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(DESIGNATOR.get())):List.of();}
    private DesignatorContent(){}
}
