package com.hbm.item.bedrockprocessing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BedrockProcessingContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredItem<LegacyBedrockItem>> LEGACY=new LinkedHashMap<>();
    public static final Map<String,DeferredItem<EnrichedBedrockItem>> ENRICHED=new LinkedHashMap<>();
    static {
        for(String symbol:List.of("ore_bedrock","ore_centrifuged","ore_cleaned","ore_separated","ore_purified","ore_nitrated","ore_nitrocrystalline","ore_deepcleaned","ore_seared"))registerStage(symbol);
        var ores=BedrockProcessingData.DATA.oldOres();
        for(int ordinal=0;ordinal<ores.size();ordinal++){
            final int fixed=ordinal;
            String name=ores.get(ordinal).name().toLowerCase(java.util.Locale.ROOT);
            ENRICHED.put(name,ITEMS.register("ore_enriched_"+name,()->new EnrichedBedrockItem(fixed)));
        }
        registerStage("ore_byproduct");
    }
    private static void registerStage(String symbol){LEGACY.put(symbol,ITEMS.register(symbol.equals("ore_bedrock")?"legacy/ore_bedrock":symbol,()->new LegacyBedrockItem(symbol,symbol.equals("ore_byproduct"))));}
    public static final DeferredItem<ProcessedBedrockItem> ORE=ITEMS.register("bedrock_ore",ProcessedBedrockItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);BedrockFragmentRecipes.register();}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<Item> subtypedItems(){var result=new ArrayList<Item>();LEGACY.values().forEach(holder->result.add(holder.get()));result.add(ORE.get());return List.copyOf(result);}
    public static List<Item> allItems(){var result=new ArrayList<Item>();for(var row:LEGACY.entrySet()){if(row.getKey().equals("ore_byproduct"))ENRICHED.values().forEach(holder->result.add(holder.get()));result.add(row.getValue().get());}result.add(ORE.get());return List.copyOf(result);}
    public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>();for(var row:LEGACY.entrySet()){if(row.getKey().equals("ore_byproduct"))ENRICHED.values().forEach(holder->result.add(new ItemStack(holder.get())));result.addAll(row.getValue().get().variants());}result.addAll(ORE.get().variants());return List.copyOf(result);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?ORE.get().variants():List.of();}
    private BedrockProcessingContent(){}
}
