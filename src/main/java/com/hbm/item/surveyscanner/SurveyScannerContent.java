package com.hbm.item.surveyscanner;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source ModItems3836; no duplicate bedrock density scanner or starter-kit registry. */
public final class SurveyScannerContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SurveyScannerItem> SCANNER = ITEMS.register("survey_scanner", SurveyScannerItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? List.of(new ItemStack(SCANNER.get())) : List.of(); }
    private SurveyScannerContent() { }
}
