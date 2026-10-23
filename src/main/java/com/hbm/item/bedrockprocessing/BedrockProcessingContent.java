package com.hbm.item.bedrockprocessing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BedrockProcessingContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredItem<LegacyBedrockItem>> LEGACY=new LinkedHashMap<>();
    static {for(String symbol:List.of("ore_bedrock","ore_centrifuged","ore_cleaned","ore_separated","ore_purified","ore_nitrated","ore_nitrocrystalline","ore_deepcleaned","ore_seared","ore_enriched","ore_byproduct"))LEGACY.put(symbol,ITEMS.register(symbol.equals("ore_bedrock")?"legacy/ore_bedrock":symbol,()->new LegacyBedrockItem(symbol,symbol.equals("ore_byproduct"))));}
    public static final DeferredItem<ProcessedBedrockItem> ORE=ITEMS.register("bedrock_ore",ProcessedBedrockItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(BedrockProcessingContent::setup);BedrockFragmentRecipes.register();}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        var names=BedrockProcessingData.DATA.oldOres().stream().map(ore->com.hbm.registry.HbmOreDictionary.registerName("ore"+ore.oreName())).toList();
        com.hbm.registry.HbmOreDictionary.addProvider(stack->{int meta=BedrockProcessingData.metadata(stack);return stack.is(LEGACY.get("ore_enriched").get())&&meta>=0&&meta<names.size()?List.of(names.get(meta)):List.of();},tag->{int index=names.indexOf(tag);return index<0?java.util.stream.Stream.empty():java.util.stream.Stream.of(LEGACY.get("ore_enriched").get().variant(index));});
    });}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<Item> allItems(){var result=new ArrayList<Item>();LEGACY.values().forEach(holder->result.add(holder.get()));result.add(ORE.get());return List.copyOf(result);}
    public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>();LEGACY.values().forEach(holder->result.addAll(holder.get().variants()));result.addAll(ORE.get().variants());return List.copyOf(result);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?ORE.get().variants():List.of();}
    private BedrockProcessingContent(){}
}
