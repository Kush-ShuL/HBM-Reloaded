package com.hbm.item.chainsaw;

import com.hbm.item.toolability.ItemToolAbilityFueled;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ChainsawContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ChainsawItem> CHAINSAW=ITEMS.register("chainsaw",ChainsawItem::new);
    private static boolean registered;
    private ChainsawContent() { }
    public static void register(IEventBus bus) {
        if(registered)return;registered=true;ITEMS.register(bus);bus.addListener(ChainsawAnimationPacket::register);bus.addListener(ChainsawContent::creative);
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        // No source setCreativeTab override: ItemTool's original default is vanilla tools.
        if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))event.accept(new ItemStack(CHAINSAW.get()));
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(CHAINSAW.get())); }
    public static ItemStack emptyTool() { return ItemToolAbilityFueled.getEmptyTool(CHAINSAW.get()); }
}
