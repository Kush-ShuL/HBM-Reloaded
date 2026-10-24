package com.hbm.machine.plasmaforge;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.item.fluid.*;
import com.hbm.recipe.generic.*;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Original replacement-file semantics, source mode switches and live-fluid packaging loop. Source has no assembly IMC handler. */
public final class PlasmaForgeRecipeConfig {
    public static boolean enable528,enableExpensiveMode;private static final Map<RecipeManager,Set<ResourceLocation>> INSTALLED=new WeakHashMap<>();private static final Map<String,String> ALIASES=new LinkedHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
    private PlasmaForgeRecipeConfig(){}
    public static void register(){try(var input=PlasmaForgeRecipeConfig.class.getResourceAsStream("/hbm/plasma-forge-item-aliases.json")){if(input!=null)JsonParser.parseReader(new java.io.InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener(PlasmaForgeRecipeConfig::starting);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,PlasmaForgeRecipeConfig::sync);}
    public static void setup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event){event.enqueueWork(()->enableExpensiveMode=com.hbm.machine.assembler.AssemblerRecipeConfig.enableExpensiveMode);}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static List<PlasmaForgeRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(PlasmaForgeContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    private static void starting(ServerAboutToStartEvent event){install(event.getServer());}
    private static void sync(OnDatapackSyncEvent event){var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(holder.value().getType()!=PlasmaForgeContent.TYPE.get())merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<PlasmaForgeRecipe>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),file=directory.resolve("hbmPlasmaForge.json");try{Files.createDirectories(directory);if(Files.isRegularFile(file)){JsonObject root;try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}var names=new HashSet<String>();int index=0;for(var value:root.getAsJsonArray("recipes")){try{var data=GenericRecipeConfigCodec.read(value.getAsJsonObject(),server.registryAccess(),ALIASES);if(!names.add(data.name()))throw new IllegalArgumentException("Duplicate source recipe name "+data.name());data=withOrder(data,index);put(additions,"config/"+index,new PlasmaForgeRecipe(data,value.getAsJsonObject().get("ignitionTemp").getAsLong()));}catch(Exception error){UNRESOLVED.add("config row"+index+": "+error.getMessage());}index++;}}else{for(var holder:manager.getAllRecipesFor(PlasmaForgeContent.TYPE.get())){additions.put(holder.id(),new RecipeHolder<>(holder.id(),holder.value().configured(com.hbm.config.GeneralModes.expensive())));}writeTemplate(directory.resolve("_hbmPlasmaForge.json"),additions.values(),server.registryAccess());}}catch(Exception error){LogUtils.getLogger().error("Cannot load original plasma forge recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,Set.copyOf(additions.keySet()));Blueprints.registerRecipes(ResourceLocation.fromNamespaceAndPath("hbm","plasma_forge"),recipes(manager));for(String unresolved:UNRESOLVED)LogUtils.getLogger().warn("Unbound plasma forge recipe: {}",unresolved);}
    private static GenericRecipeData withOrder(GenericRecipeData d,int order){return new GenericRecipeData(d.name(),d.duration(),d.power(),d.inputItems(),d.inputFluids(),d.outputItems(),d.outputFluids(),d.pools(),d.group(),d.icon(),d.named(),d.nameWrapper(),order);}
    private static void put(Map<ResourceLocation,RecipeHolder<PlasmaForgeRecipe>> recipes,String path,PlasmaForgeRecipe recipe){var id=ResourceLocation.fromNamespaceAndPath("hbm","plasma_forge/"+path);recipes.put(id,new RecipeHolder<>(id,recipe));}
    private static void writeTemplate(Path path,Collection<RecipeHolder<PlasmaForgeRecipe>> recipes,net.minecraft.core.HolderLookup.Provider lookup)throws java.io.IOException{var root=new JsonObject();root.addProperty("comment","改名为 hbmPlasmaForge.json 后替换完整默认配方表。输入按位置匹配，流体池名称区分大小写。");var values=new JsonArray();for(var holder:recipes)try{var row=GenericRecipeConfigCodec.write(holder.value().data(),lookup);row.addProperty("ignitionTemp",holder.value().ignitionTemp());values.add(row);}catch(IllegalArgumentException error){UNRESOLVED.add("template "+holder.id()+": "+error.getMessage());}root.add("recipes",values);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
}
