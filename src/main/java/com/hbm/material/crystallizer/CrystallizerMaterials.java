package com.hbm.material.crystallizer;
import com.hbm.registry.HbmOreDictionary;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class CrystallizerMaterials {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<GraphiteBlock> GRAPHITE=BLOCKS.register("block_graphite",GraphiteBlock::new);public static final DeferredItem<BlockItem> GRAPHITE_ITEM=ITEMS.registerSimpleBlockItem(GRAPHITE);public static final List<DeferredItem<PlasticScrapItem>> PLASTIC=PlasticScrapItem.NAMES.stream().map(name->ITEMS.register("scrap_plastic_"+name,PlasticScrapItem::new)).toList();
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->{HbmOreDictionary.registerName("blockGraphite");HbmOreDictionary.registerName("blockCarbon");}));}
 public static List<ItemStack> allStacks(){var stacks=new ArrayList<>(PLASTIC.stream().map(item->new ItemStack(item.get())).toList());stacks.add(new ItemStack(GRAPHITE_ITEM.get()));return stacks;}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(GRAPHITE_ITEM.get())):List.of();}
 private CrystallizerMaterials(){}
}
