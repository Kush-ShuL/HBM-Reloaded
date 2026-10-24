package com.hbm.world.glyphidhive;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** One source hive material block with three physical fixed item identities. */
public final class GlyphidHiveMaterialContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GlyphidHiveMaterialBlock> BASE=BLOCKS.register("glyphid_base",GlyphidHiveMaterialBlock::new);
    public static final DeferredItem<GlyphidHiveMaterialItem> ITEM_STANDARD=ITEMS.register("glyphid_base_standard",()->new GlyphidHiveMaterialItem(BASE.get(),GlyphidHiveMaterialItem.Kind.STANDARD));
    public static final DeferredItem<GlyphidHiveMaterialItem> ITEM_INFESTED=ITEMS.register("glyphid_base_infested",()->new GlyphidHiveMaterialItem(BASE.get(),GlyphidHiveMaterialItem.Kind.INFESTED));
    public static final DeferredItem<GlyphidHiveMaterialItem> ITEM_RADIOACTIVE=ITEMS.register("glyphid_base_radioactive",()->new GlyphidHiveMaterialItem(BASE.get(),GlyphidHiveMaterialItem.Kind.RADIOACTIVE));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<GlyphidHiveMaterialItem> items(){return List.of(ITEM_STANDARD.get(),ITEM_INFESTED.get(),ITEM_RADIOACTIVE.get());}
    public static List<ItemStack> allStacks(){return items().stream().map(ItemStack::new).toList();}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?allStacks():List.of();}
    private GlyphidHiveMaterialContent(){}
}
