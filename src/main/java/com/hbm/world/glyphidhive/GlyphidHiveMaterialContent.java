package com.hbm.world.glyphidhive;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Phase1 actual source material only. No unimplemented spawner, entity, natural entry or substitute is registered. */
public final class GlyphidHiveMaterialContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GlyphidHiveMaterialBlock> BASE=BLOCKS.register("glyphid_base",GlyphidHiveMaterialBlock::new);
    public static final DeferredItem<GlyphidHiveMaterialItem> ITEM=ITEMS.register("glyphid_base",()->new GlyphidHiveMaterialItem(BASE.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> allStacks(){return java.util.stream.IntStream.range(0,3).mapToObj(meta->GlyphidHiveMaterialItem.stack(meta,1)).toList();}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?allStacks():List.of();}
    private GlyphidHiveMaterialContent(){}
}
