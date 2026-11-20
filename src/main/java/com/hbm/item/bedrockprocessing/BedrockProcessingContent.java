package com.hbm.item.bedrockprocessing;

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
    public static final Map<String,DeferredItem<ProcessedBedrockItem>> PROCESSED=new LinkedHashMap<>();
    static {
        for(var type:BedrockProcessingData.DATA.types())for(var grade:BedrockProcessingData.DATA.grades()){
            String key=grade.name()+"/"+type.name();
            PROCESSED.put(key,ITEMS.register("bedrock_ore/"+key.toLowerCase(Locale.ROOT),()->new ProcessedBedrockItem(grade,type)));
        }
    }
    public static ProcessedBedrockItem processed(String grade,String type){var item=PROCESSED.get(grade+"/"+type);if(item==null)throw new IllegalArgumentException("Unknown fixed processed bedrock identity "+grade+"/"+type);return item.get();}
    public static void register(IEventBus bus){ITEMS.register(bus);BedrockFragmentRecipes.register();}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<Item> allItems(){return PROCESSED.values().stream().<Item>map(DeferredItem::get).toList();}
    public static List<ItemStack> indexedItems(){return allItems().stream().map(ItemStack::new).toList();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?PROCESSED.values().stream().map(holder->new ItemStack(holder.get())).toList():List.of();}
    private BedrockProcessingContent(){}
}
