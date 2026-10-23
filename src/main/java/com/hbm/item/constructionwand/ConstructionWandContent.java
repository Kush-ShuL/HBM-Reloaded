package com.hbm.item.constructionwand;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConstructionWandContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ConstructionWandItem> WAND=ITEMS.register("wand_k",ConstructionWandItem::new);
    public static final DeferredBlock<WandAirMarkerBlock> AIR_MARKER=BLOCKS.register("wand_air",WandAirMarkerBlock::new);
    public static final DeferredItem<BlockItem> AIR_ITEM=ITEMS.register("wand_air",()->new BlockItem(AIR_MARKER.get(),new Item.Properties()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);NeoForge.EVENT_BUS.addListener(ConstructionWandFills::loaded);NeoForge.EVENT_BUS.addListener(ConstructionWandFills::tick);NeoForge.EVENT_BUS.addListener(ConstructionWandFills::unload);NeoForge.EVENT_BUS.addListener(ConstructionWandFills::stopped);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(WAND.get()),new ItemStack(AIR_ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "consumable".equals(tab)?List.of(new ItemStack(WAND.get())):List.of();}
    private ConstructionWandContent(){}
}
