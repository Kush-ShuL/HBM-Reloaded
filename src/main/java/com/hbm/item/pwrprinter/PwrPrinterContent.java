package com.hbm.item.pwrprinter;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original creative controlTab tool; the supplied source has no crafting recipe or NEI category. */
public final class PwrPrinterContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<PwrPrinterItem> ITEM=ITEMS.register("pwr_printer",PwrPrinterItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(PwrPrinterPackets::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(ITEM.get())):List.of();}
    private PwrPrinterContent(){}
}
