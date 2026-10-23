package com.hbm.equipment.jetpack;

import com.hbm.item.medicine.MedicineItem;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Vector and its two real manufacture predecessors, plus the existing syringe backend's kerosene tank. */
public final class VectorJetpackContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceJetpackItem> REGULAR = ITEMS.register("jetpack_fly", () -> new SourceJetpackItem(SourceJetpackItem.Kind.REGULAR));
    public static final DeferredItem<SourceJetpackItem> BRAKE = ITEMS.register("jetpack_break", () -> new SourceJetpackItem(SourceJetpackItem.Kind.BRAKE));
    public static final DeferredItem<SourceJetpackItem> VECTOR = ITEMS.register("jetpack_vector", () -> new SourceJetpackItem(SourceJetpackItem.Kind.VECTOR));
    public static final DeferredItem<MedicineItem> TANK = ITEMS.register("jetpack_tank", MedicineItem::sourceJetpackTank);
    public static void register(IEventBus bus) {
        ITEMS.register(bus); JetpackNetwork.register(bus); bus.addListener(VectorJetpackContent::vanillaCreative);
        NeoForge.EVENT_BUS.addListener(JetpackRuntime::tick); NeoForge.EVENT_BUS.addListener(JetpackNetwork::login);
        NeoForge.EVENT_BUS.addListener(JetpackNetwork::respawn); NeoForge.EVENT_BUS.addListener(JetpackNetwork::clonePlayer);
        NeoForge.EVENT_BUS.addListener(JetpackNetwork::dimension);
    }
    private static void vanillaCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) { event.accept(REGULAR.get()); event.accept(BRAKE.get()); event.accept(VECTOR.get()); }
    }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? List.of(new ItemStack(TANK.get())) : List.of(); }
    private VectorJetpackContent() { }
}
