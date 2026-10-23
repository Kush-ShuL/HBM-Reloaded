package com.hbm.machine.reactor.iter;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
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

/** Original hbmFusion.json whole-list replacement and all six exact FusionRecipes extra fields. */
public final class FusionRecipeConfig {
    private static final Map<RecipeManager,Set<ResourceLocation>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private static long maxInput;
    public static void register(){try(var stream=FusionRecipeConfig.class.getResourceAsStream("/hbm/iter-fusion-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing actual ITER recipe aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener(FusionRecipeConfig::starting);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,FusionRecipeConfig::sync);}
    public static List<FusionRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(IterRecipes.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}public static long maxInput(){return maxInput;}
    private static void starting(ServerAboutToStartEvent event){install(event.getServer());}
    private static void sync(OnDatapackSyncEvent event){var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);}
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(holder.value().getType()!=IterRecipes.TYPE.get())merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<FusionRecipe>>();UNRESOLVED.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var file=directory.resolve("hbmFusion.json");try{Files.createDirectories(directory);if(Files.isRegularFile(file)){JsonObject root;try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}var names=new HashSet<String>();int index=0;for(var value:root.getAsJsonArray("recipes")){try{var object=value.getAsJsonObject();var data=GenericRecipeConfigCodec.read(object,server.registryAccess(),ALIASES);if(!names.add(data.name()))throw new IllegalArgumentException("Duplicate actual fusion recipe "+data.name());data=new GenericRecipeData(data.name(),data.duration(),data.power(),data.inputItems(),data.inputFluids(),data.outputItems(),data.outputFluids(),data.pools(),data.group(),data.icon(),data.named(),data.nameWrapper(),index);var recipe=new FusionRecipe(data,object.get("ignitionTemp").getAsLong(),object.get("outputTemp").getAsLong(),object.get("outputFlux").getAsDouble(),object.get("r").getAsFloat(),object.get("g").getAsFloat(),object.get("b").getAsFloat());var id=ResourceLocation.parse("hbm:fusion/config/"+index);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("config row"+index+": "+error.getMessage());}index++;}}else{for(var holder:manager.getAllRecipesFor(IterRecipes.TYPE.get()))additions.put(holder.id(),holder);writeTemplate(directory.resolve("_hbmFusion.json"),additions.values());}}catch(Exception error){LogUtils.getLogger().error("Cannot load original fusion recipes",error);}maxInput=0;for(var holder:additions.values())maxInput=Math.max(maxInput,holder.value().ignitionTemp());additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,Set.copyOf(additions.keySet()));Blueprints.registerRecipes(ResourceLocation.parse("hbm:fusion"),recipes(manager));for(var unresolved:UNRESOLVED)LogUtils.getLogger().warn("Unbound fusion recipe: {}",unresolved);}
    private static void writeTemplate(Path file,Collection<RecipeHolder<FusionRecipe>> recipes)throws java.io.IOException{var root=new JsonObject();root.addProperty("comment","改名为 hbmFusion.json 后替换完整默认配方表。三个输入按位置匹配；点火能量、热输出和中子通量分别配置。");var values=new JsonArray();for(var holder:recipes)try{var recipe=holder.value();var row=GenericRecipeConfigCodec.write(recipe.data());row.addProperty("ignitionTemp",recipe.ignitionTemp());row.addProperty("outputTemp",recipe.outputTemp());row.addProperty("outputFlux",recipe.neutronFlux());row.addProperty("r",recipe.r());row.addProperty("g",recipe.g());row.addProperty("b",recipe.b());values.add(row);}catch(IllegalArgumentException error){UNRESOLVED.add("template "+holder.id()+": "+error.getMessage());}root.add("recipes",values);Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
    private FusionRecipeConfig(){}
}
