package com.hbm.block.toaster;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The actual three source decorative toaster subtypes; no BE, invented machine or new manufacture. */
public final class ToasterContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ToasterBlock> BLOCK = BLOCKS.register("deco_toaster", ToasterBlock::new);
    public static final DeferredItem<ToasterItem> ITEM_IRON = ITEMS.register("deco_toaster_iron", () -> new ToasterItem(BLOCK.get(), ToasterItem.Kind.IRON));
    public static final DeferredItem<ToasterItem> ITEM_STEEL = ITEMS.register("deco_toaster_steel", () -> new ToasterItem(BLOCK.get(), ToasterItem.Kind.STEEL));
    public static final DeferredItem<ToasterItem> ITEM_WOOD = ITEMS.register("deco_toaster_wood", () -> new ToasterItem(BLOCK.get(), ToasterItem.Kind.WOOD));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ToasterItem> items() { return List.of(ITEM_IRON.get(), ITEM_STEEL.get(), ITEM_WOOD.get()); }
    public static ToasterItem item(int type) { return items().get(type); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("block") ? items().stream().map(ItemStack::new).toList() : List.of(); }
    private ToasterContent() {}
}
