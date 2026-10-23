package com.hbm.machine.reactor;
import com.google.gson.*;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.mojang.logging.LogUtils;
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
/** Original hbmBreeder.json input/flux/output arrays; recipes synchronize through the native recipe manager. */
public final class BreedingRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNBOUND=new ArrayList<>();
    private BreedingRecipeConfig(){}
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<BreedingRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(ReactorContent.BREEDING_TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(BreedingRecipe::order)).toList();}
    public static List<String> unbound(){return List.copyOf(UNBOUND);}
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNBOUND.clear();
        var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=directory.resolve("hbmBreeder.json");
        try{Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==ReactorContent.BREEDING_TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{try(var stream=BreedingRecipeConfig.class.getResourceAsStream("/hbm/reactors-breeding-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source breeder recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmBreeder.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=row.getAsJsonArray("input");if(!input.get(0).getAsString().equals("item"))throw new IllegalArgumentException("Original BreederRecipes casts input to ComparableStack; only item input is supported");var recipe=new BreedingRecipe(GenericRecipeConfigCodec.readIngredient(input,Map.of()).ingredient(),GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),Map.of()),row.get("flux").getAsInt(),order);var id=ResourceLocation.fromNamespaceAndPath("hbm","research_breeding/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNBOUND.add("row "+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load source research breeder recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);for(var error:UNBOUND)LogUtils.getLogger().warn("Unbound source research breeder recipe: {}",error);
    }
}
