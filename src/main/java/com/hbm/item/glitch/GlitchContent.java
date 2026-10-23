package com.hbm.item.glitch;

import com.hbm.item.singularity.SourceSingularityItem;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GlitchContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceSingularityItem> BETA = ITEMS.register("beta", () -> new SourceSingularityItem(SourceSingularityItem.Kind.BETA));
    public static final DeferredItem<GlitchItem> GLITCH = ITEMS.register("glitch", GlitchItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(BetaRuntime::tick); net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(BetaRuntime::respawn); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(BETA.get()), new ItemStack(GLITCH.get())); }
    /** Beta had no source creative tab; Glitch alone belongs to the source consumable tab. */
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? List.of(new ItemStack(GLITCH.get())) : List.of(); }
    private GlitchContent() { }
}
