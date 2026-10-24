package com.hbm.item.bedrockprocessing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    public static final Map<String,DeferredItem<ProcessedBedrockItem>> PROCESSED=new LinkedHashMap<>();
    static {
        for(String symbol:List.of("ore_bedrock","ore_centrifuged","ore_cleaned","ore_separated","ore_purified","ore_nitrated","ore_nitrocrystalline","ore_deepcleaned","ore_seared"))registerStage(symbol);
        var ores=BedrockProcessingData.DATA.oldOres();
        for(int ordinal=0;ordinal<ores.size();ordinal++){
            final int fixed=ordinal;
            String name=ores.get(ordinal).name().toLowerCase(Locale.ROOT);
            ENRICHED.put(name,ITEMS.register("ore_enriched_"+name,()->new EnrichedBedrockItem(fixed)));
        }
        registerStage("ore_byproduct");
        for(var type:BedrockProcessingData.DATA.types())for(var grade:BedrockProcessingData.DATA.grades()){
            String key=grade.name()+"/"+type.name();
            PROCESSED.put(key,ITEMS.register("bedrock_ore/"+key.toLowerCase(Locale.ROOT),()->new ProcessedBedrockItem(grade,type)));
        }
    }
    private static void registerStage(String symbol){
        var names=symbol.equals("ore_byproduct")?BedrockProcessingData.DATA.byproducts().stream().map(row->row.name().substring(2)).toList():BedrockProcessingData.DATA.oldOres().stream().map(BedrockProcessingData.OldOre::name).toList();
        for(int ordinal=0;ordinal<names.size();ordinal++){
            final int fixed=ordinal;String path=symbol+"_"+names.get(ordinal).toLowerCase(Locale.ROOT);
            LEGACY.put(path,ITEMS.register(path,()->new LegacyBedrockItem(symbol,fixed)));
        }
    }
    public static ProcessedBedrockItem processed(String grade,String type){var item=PROCESSED.get(grade+"/"+type);if(item==null)throw new IllegalArgumentException("Unknown fixed processed bedrock identity "+grade+"/"+type);return item.get();}
    public static void register(IEventBus bus){ITEMS.register(bus);BedrockFragmentRecipes.register();}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<Item> allItems(){
        var result=new ArrayList<Item>();
        LEGACY.values().forEach(holder->{if(!holder.get().byproduct)result.add(holder.get());});
        ENRICHED.values().forEach(holder->result.add(holder.get()));
        LEGACY.values().forEach(holder->{if(holder.get().byproduct)result.add(holder.get());});
        PROCESSED.values().forEach(holder->result.add(holder.get()));return List.copyOf(result);
    }
    public static List<ItemStack> indexedItems(){return allItems().stream().map(ItemStack::new).toList();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?PROCESSED.values().stream().map(holder->new ItemStack(holder.get())).toList():List.of();}
    private BedrockProcessingContent(){}
}
