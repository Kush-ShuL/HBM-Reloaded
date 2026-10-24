package com.hbm.machine.purex;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original hbmPUREX.json replacement grammar, source mode defaults, and recipe synchronization. */
public final class PurexRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    
    private PurexRecipeConfig(){}
    public static void register(){
        try(var stream=PurexRecipeConfig.class.getResourceAsStream("/hbm/purex-item-aliases.json")){
            if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<PurexRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(PurexContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmPUREX.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){
                merged.values().removeIf(holder->holder.value().getType()==PurexContent.TYPE.get());
                try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            }else{
                try(var stream=PurexRecipeConfig.class.getResourceAsStream("/hbm/purex-recipes.json")){
                    if(stream==null)throw new IllegalStateException("Missing original PUREX source recipes");
                    root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().deepCopy();
                }
                addNaquadriaRecipes(root);
                root.addProperty("comment","改名为 hbmPUREX.json 后替换完整默认配方表。输入按位置匹配，压力和流体池名称须准确填写。");
                Files.writeString(directory.resolve("_hbmPUREX.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
            }
            var names=new HashSet<String>();int order=0;
            for(var value:root.getAsJsonArray("recipes")){
                String label="row "+order;
                try{
                    var row=value.getAsJsonObject();label=row.has("name")?row.get("name").getAsString():label;
                    var d=GenericRecipeConfigCodec.read(row,server.registryAccess(),ALIASES);
                    var icon=d.icon();if(row.has("iconFirstInput")&&row.get("iconFirstInput").getAsBoolean()&&!d.inputItems().isEmpty()){var examples=d.inputItems().getFirst().examples();if(!examples.isEmpty())icon=examples.getFirst().copy();}
                    if(!names.add(d.name()))throw new IllegalArgumentException("Duplicate source recipe "+d.name());
                    d=new GenericRecipeData(d.name(),d.duration(),d.power(),d.inputItems(),d.inputFluids(),d.outputItems(),d.outputFluids(),d.pools(),d.group(),icon,d.named(),d.nameWrapper(),order);
                    var id=ResourceLocation.fromNamespaceAndPath("hbm","purex/"+(Files.isRegularFile(active)?"config/":"default/")+order);
                    additions.put(id,new RecipeHolder<>(id,new PurexRecipe(d)));
                }catch(Exception error){UNRESOLVED.add(label+": "+error.getMessage());}
                order++;
            }
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original PUREX recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);
        Blueprints.registerRecipes(ResourceLocation.fromNamespaceAndPath("hbm","purex"),recipes(manager));
        for(var error:UNRESOLVED)LogUtils.getLogger().warn("Unbound PUREX recipe: {}",error);
    }
    private static void addNaquadriaRecipes(JsonObject root){
        var nuggets=com.hbm.registry.HbmOreDictionary.items(com.hbm.registry.HbmOreDictionary.registerName("nuggetNaquadria")).findFirst();
        var conditional=root.getAsJsonArray("conditionalNaquadria");root.remove("conditionalNaquadria");if(nuggets.isEmpty()||conditional==null)return;
        for(var value:conditional){var row=value.getAsJsonObject().deepCopy();if(row.has("naquadriaOutputCount")){var item=nuggets.get().copyWithCount(row.remove("naquadriaOutputCount").getAsInt());var output=new JsonArray();output.add("single");output.add(GenericRecipeConfigCodec.writeStack(item));row.getAsJsonArray("outputItem").set(0,output);}root.getAsJsonArray("recipes").add(row);}
        var sorted=new JsonArray();java.util.stream.StreamSupport.stream(root.getAsJsonArray("recipes").spliterator(),false).sorted(java.util.Comparator.comparingInt(value->value.getAsJsonObject().get("sourceOrder").getAsInt())).forEach(sorted::add);root.add("recipes",sorted);
    }

}
