package com.hbm.machine.supercomputer;

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

/** Original hbmSuperComputer.json whole-list replacement and GenericRecipe weighted outputs. */
public final class SuperComputerRecipeConfig {
    private static final Map<RecipeManager,Set<ResourceLocation>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();

    public static void register(){try(var stream=SuperComputerRecipeConfig.class.getResourceAsStream("/hbm/supercomputer-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing actual SuperComputer recipe aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener(SuperComputerRecipeConfig::starting);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,SuperComputerRecipeConfig::sync);}
    public static List<SuperComputerRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(SuperComputerRecipes.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static void starting(ServerAboutToStartEvent event){install(event.getServer());}
    private static void sync(OnDatapackSyncEvent event){var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(holder.value().getType()!=SuperComputerRecipes.TYPE.get())merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<SuperComputerRecipe>>();UNRESOLVED.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var file=directory.resolve("hbmSuperComputer.json");try{Files.createDirectories(directory);if(Files.isRegularFile(file)){JsonObject root;try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}var names=new HashSet<String>();int index=0;for(var value:root.getAsJsonArray("recipes")){try{var object=value.getAsJsonObject();var data=GenericRecipeConfigCodec.read(object,server.registryAccess(),ALIASES);if(!names.add(data.name()))throw new IllegalArgumentException("Duplicate actual supercomputer recipe "+data.name());data=new GenericRecipeData(data.name(),data.duration(),data.power(),data.inputItems(),data.inputFluids(),data.outputItems(),data.outputFluids(),data.pools(),data.group(),data.icon(),data.named(),data.nameWrapper(),index);var recipe=new SuperComputerRecipe(data);var id=ResourceLocation.parse("hbm:supercomputer/config/"+index);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("config row"+index+": "+error.getMessage());}index++;}}else{for(var holder:manager.getAllRecipesFor(SuperComputerRecipes.TYPE.get()))additions.put(holder.id(),holder);writeTemplate(directory.resolve("_hbmSuperComputer.json"),additions.values(),server.registryAccess());}}catch(Exception error){LogUtils.getLogger().error("Cannot load original supercomputer recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,Set.copyOf(additions.keySet()));Blueprints.registerRecipes(ResourceLocation.parse("hbm:supercomputer"),recipes(manager));for(var unresolved:UNRESOLVED)LogUtils.getLogger().warn("Unbound supercomputer recipe: {}",unresolved);}
    private static void writeTemplate(Path file,Collection<RecipeHolder<SuperComputerRecipe>> recipes,net.minecraft.core.HolderLookup.Provider lookup)throws java.io.IOException{var root=new JsonObject();root.addProperty("comment","改名为 hbmSuperComputer.json 后替换完整默认配方表。三个输入按位置匹配，成功与损坏驱动器按权重产出。");var values=new JsonArray();for(var holder:recipes)try{var recipe=holder.value();var row=GenericRecipeConfigCodec.write(recipe.data(),lookup);values.add(row);}catch(IllegalArgumentException error){UNRESOLVED.add("template "+holder.id()+": "+error.getMessage());}root.add("recipes",values);Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
    private SuperComputerRecipeConfig(){}
}
