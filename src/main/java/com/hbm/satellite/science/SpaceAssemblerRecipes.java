package com.hbm.satellite.science;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Actual one source default and replacement hbmSpaceAssembler.json, installed/synchronized as native recipes. */
public final class SpaceAssemblerRecipes {
    private record Snapshot(List<SpaceAssemblerRecipe> ordered,Map<String,SpaceAssemblerRecipe> names){}
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<RecipeManager,Snapshot> SNAPSHOTS=Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    // GenericRecipes.RNG is independent of world.rand. Reuse the sole GenericOutput algorithm with an independent source stream.
    private static final RandomSource OUTPUT_RANDOM=RandomSource.create();
    private static volatile Snapshot loaded=new Snapshot(List.of(),Map.of());
    public static void register(){
        try(var stream=SpaceAssemblerRecipes.class.getResourceAsStream("/hbm/satellite-science107-item-aliases.json")){
            if(stream==null)throw new IllegalStateException("Missing actual space assembly source aliases");
            JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(e->ALIASES.put(e.getKey(),e.getValue().getAsString()));
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
        // Before any dimension's SatelliteSavedData is requested/ticked, named task restoration has the actual configured table.
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{
            var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);
        });
    }
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static List<SpaceAssemblerRecipe> recipes(RecipeManager manager){return collect(manager).ordered;}
    public static SpaceAssemblerRecipe getRecipe(ServerLevel world,ItemStack part){for(var recipe:snapshot(world.getRecipeManager()).ordered)if(recipe.inputItems().size()==1&&recipe.inputItems().getFirst().matches(part,true))return recipe;return null;}
    public static SpaceAssemblerRecipe byName(ServerLevel world,String name){return snapshot(world.getRecipeManager()).names.get(name);}
    public static SpaceAssemblerRecipe loadedByName(String name){return loaded.names.get(name);}
    public static ItemStack collapse(GenericOutput output){return output.collapse(OUTPUT_RANDOM);}
    private static Snapshot snapshot(RecipeManager manager){return SNAPSHOTS.computeIfAbsent(manager,SpaceAssemblerRecipes::collect);}
    private static Snapshot collect(RecipeManager manager){
        var ordered=manager.getAllRecipesFor(ScienceSatelliteContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();
        var names=new LinkedHashMap<String,SpaceAssemblerRecipe>();
        for(var recipe:ordered)if(names.putIfAbsent(recipe.name(),recipe)!=null)throw new IllegalStateException("Duplicate original space assembly name: "+recipe.name());
        return new Snapshot(ordered,Collections.unmodifiableMap(names));
    }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmSpaceAssembler.json");boolean custom=Files.isRegularFile(active);
        if(custom)merged.values().removeIf(holder->holder.value().getType()==ScienceSatelliteContent.TYPE.get());
        try{
            Files.createDirectories(directory);JsonObject root;
            if(custom)try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            else try(var stream=SpaceAssemblerRecipes.class.getResourceAsStream("/hbm/satellite-science107-source-recipes.json")){
                if(stream==null)throw new IllegalStateException("Missing actual source space assembly recipes");
                root=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
            }
            var names=new HashSet<String>();int order=0;
            for(var value:root.getAsJsonArray("recipes")){
                int index=order++;
                try{
                    JsonObject row=value.getAsJsonObject().deepCopy();
                    // Source GenericRecipes ignores these arrays when the declared fluid limits are zero.
                    row.remove("inputFluid");row.remove("outputFluid");row.addProperty("order",index);
                    if(!row.has("duration")||!row.has("power"))throw new IllegalArgumentException("Original duration/power fields are required");
                    var data=GenericRecipeConfigCodec.read(row,server.registryAccess(),ALIASES);
                    if(!names.add(data.name()))throw new IllegalArgumentException("Duplicate source recipe name "+data.name());
                    var recipe=new SpaceAssemblerRecipe(data);var id=ResourceLocation.fromNamespaceAndPath("hbm","space_assembler/"+(custom?"config/":"default/")+index);
                    additions.put(id,new RecipeHolder<>(id,recipe));
                }catch(Exception error){UNRESOLVED.add("row "+index+": "+error.getMessage());}
            }
            if(!custom){var template=new JsonObject();var rows=new JsonArray();for(var holder:additions.values())rows.add(GenericRecipeConfigCodec.write(((SpaceAssemblerRecipe)holder.value()).data()));template.add("recipes",rows);Files.writeString(directory.resolve("_hbmSpaceAssembler.json"),new GsonBuilder().setPrettyPrinting().create().toJson(template)+"\n");}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load actual source space assembly recipes",error);UNRESOLVED.add(error.toString());}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);SNAPSHOTS.remove(manager);loaded=snapshot(manager);
        Blueprints.registerRecipes(ResourceLocation.parse("hbm:space_assembler"),loaded.ordered);
        for(String issue:UNRESOLVED)LogUtils.getLogger().warn("Unbound original space assembler recipe: {}",issue);
    }
    private SpaceAssemblerRecipes(){}
}
