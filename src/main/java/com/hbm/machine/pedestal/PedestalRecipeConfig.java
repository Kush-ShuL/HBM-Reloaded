package com.hbm.machine.pedestal;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original hbmPedestal.json array grammar and source order, using native synchronized recipe holders. */
public final class PedestalRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<PedestalRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(PedestalContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(PedestalRecipe::order)).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmPedestal.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){
                merged.values().removeIf(holder->holder.value().getType()==PedestalContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            }else{
                try(var stream=PedestalRecipeConfig.class.getResourceAsStream("/hbm/pedestal-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source pedestal definitions");root=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();}
                Files.writeString(directory.resolve("_hbmPedestal.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
            }
            int order=0;
            for(var value:root.getAsJsonArray("recipes")){
                try{
                    var row=value.getAsJsonObject();var output=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),Map.of());var inputs=new ArrayList<GenericIngredient>();
                    for(var in:row.getAsJsonArray("input"))inputs.add(in.isJsonNull()?new GenericIngredient(Ingredient.EMPTY,0):GenericRecipeConfigCodec.readIngredient(in.getAsJsonArray(),Map.of()));
                    var recipe=new PedestalRecipe(output,inputs,row.has("extra")?PedestalRecipe.Extra.valueOf(row.get("extra").getAsString()):PedestalRecipe.Extra.NONE,row.has("set")?row.get("set").getAsInt():0,order);
                    var id=ResourceLocation.parse("hbm:pedestal/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));
                }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}
                order++;
            }
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original pedestal recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);
        for(var error:UNRESOLVED)LogUtils.getLogger().warn("Unbound original pedestal recipe: {}",error);
    }
    private PedestalRecipeConfig(){}
}
