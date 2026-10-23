package com.hbm.block.sourcestones;

import com.hbm.block.ore.LegacyOreItem;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Two actual source block/items; no BE, extra recipe engine, material identity or dormant BiomeCave registration. */
public final class SourceStonesContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<BiomeStoneBlock> BIOME=BLOCKS.register("stone_biome",BiomeStoneBlock::new);
    public static final DeferredBlock<PorousStoneBlock> POROUS=BLOCKS.register("stone_porous",PorousStoneBlock::new);
    public static final DeferredItem<LegacyOreItem> BIOME_ITEM=ITEMS.register("stone_biome",()->new LegacyOreItem(BIOME.get()));
    public static final DeferredItem<BlockItem> POROUS_ITEM=ITEMS.register("stone_porous",()->new BlockItem(POROUS.get(),new Item.Properties()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("block"))return List.of();var result=new ArrayList<>(BIOME_ITEM.get().creativeStacks());result.add(new ItemStack(POROUS_ITEM.get()));return result;}
    public static List<ItemStack> biomeStacks(){return BIOME_ITEM.get().creativeStacks();}
    public static Map<String,Item> indexedItems(){return Map.of("stone_biome",BIOME_ITEM.get(),"stone_porous",POROUS_ITEM.get());}
    private SourceStonesContent(){}
}
