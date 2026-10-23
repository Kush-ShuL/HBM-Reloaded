package com.hbm.block.lightstonestairs;

import com.hbm.block.LegacyStairsBlock;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.world.structure.templates.materials.TemplateMaterials;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual source LightstoneType.TILE (ordinal1), separate from existing BRICKS stairs (ordinal2). */
public final class LightstoneTileStairsContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<LegacyStairsBlock> BLOCK=BLOCKS.register("lightstone_tile_stairs",()->new LegacyStairsBlock(TemplateMaterials.LIGHTSTONE.get().defaultBlockState().setValue(LegacyOreBlock.LEGACY_META,1),BlockBehaviour.Properties.ofFullCopy(TemplateMaterials.LIGHTSTONE.get()).noOcclusion()));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("lightstone_tile_stairs",BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("lightstone_tile_stairs",ITEM.get());}
    private LightstoneTileStairsContent(){}
}
