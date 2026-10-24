package com.hbm.machine.assembler;
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
public final class AssemblerRecipeConfig {
    public static boolean enable528,enableExpensiveMode;private static final Map<RecipeManager,Set<ResourceLocation>> INSTALLED=new WeakHashMap<>();private static final Map<String,String> ALIASES=new LinkedHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
    private AssemblerRecipeConfig(){}
    public static void register(){try(var input=AssemblerRecipeConfig.class.getResourceAsStream("/hbm/assembly-item-aliases.json")){if(input!=null)JsonParser.parseReader(new java.io.InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener(AssemblerRecipeConfig::starting);NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,AssemblerRecipeConfig::sync);}
    static void setup(FMLCommonSetupEvent event){event.enqueueWork(AssemblerRecipeConfig::loadMode);}
    private static void loadMode(){enable528=com.hbm.config.GeneralModes.mode528();enableExpensiveMode=com.hbm.config.GeneralModes.expensive();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static List<AssemblerRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(AssemblerContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    private static void starting(ServerAboutToStartEvent event){install(event.getServer());}
    private static void sync(OnDatapackSyncEvent event){var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){loadMode();var manager=server.getRecipeManager();var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(holder.value().getType()!=AssemblerContent.TYPE.get())merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<AssemblerRecipe>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),file=directory.resolve("hbmAssemblyMachine.json");try{Files.createDirectories(directory);if(Files.isRegularFile(file)){JsonObject root;try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}var names=new HashSet<String>();int index=0;for(var value:root.getAsJsonArray("recipes")){try{var data=GenericRecipeConfigCodec.read(value.getAsJsonObject(),server.registryAccess(),ALIASES);if(!names.add(data.name()))throw new IllegalArgumentException("Duplicate source recipe name "+data.name());data=withOrder(data,index);put(additions,"config/"+index,new AssemblerRecipe(data));}catch(Exception error){UNRESOLVED.add("config row"+index+": "+error.getMessage());}index++;}}else{for(var holder:manager.getAllRecipesFor(AssemblerContent.TYPE.get())){if(holder.value().requiresNo528()&&enable528)continue;additions.put(holder.id(),new RecipeHolder<>(holder.id(),holder.value().configured(enableExpensiveMode,enable528)));}addPackaging(additions);writeTemplate(directory.resolve("_hbmAssemblyMachine.json"),additions.values());}}catch(Exception error){LogUtils.getLogger().error("Cannot load original assembly machine recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,Set.copyOf(additions.keySet()));Blueprints.registerRecipes(ResourceLocation.fromNamespaceAndPath("hbm","assembly"),recipes(manager));for(String unresolved:UNRESOLVED)LogUtils.getLogger().warn("Unbound assembly recipe: {}",unresolved);}
    private static GenericRecipeData withOrder(GenericRecipeData d,int order){return new GenericRecipeData(d.name(),d.duration(),d.power(),d.inputItems(),d.inputFluids(),d.outputItems(),d.outputFluids(),d.pools(),d.group(),d.icon(),d.named(),d.nameWrapper(),order);}
    private static void put(Map<ResourceLocation,RecipeHolder<AssemblerRecipe>> recipes,String path,AssemblerRecipe recipe){var id=ResourceLocation.fromNamespaceAndPath("hbm","assembly/"+path);recipes.put(id,new RecipeHolder<>(id,recipe));}
    private static void addPackaging(Map<ResourceLocation,RecipeHolder<AssemblerRecipe>> recipes){ItemStack empty;try{empty=GenericRecipeConfigCodec.legacyStack("hbm:fluid_pack_empty",0,ALIASES);}catch(IllegalArgumentException error){UNRESOLVED.add("Dynamic source package/unpackage: "+error.getMessage());return;}var order=HbmFluids.getInNiceOrder();for(int i=1;i<order.length;i++){var type=order[i];if(type.hasNoContainer())continue;var full=FluidItemContent.stack(FluidItemContent.PACK.get(),type);var into=new GenericRecipeData("ass.package"+type.getUnlocalizedName(),40,100,List.of(new GenericIngredient(new AlloyStackIngredient(empty).toVanilla(),1)),List.of(new FluidStack(type,32000)),List.of(new GenericOutput(full)),List.of(),List.of(),"",ItemStack.EMPTY,false,"",10000+i*2);var out=new GenericRecipeData("ass.unpackage"+type.getUnlocalizedName(),40,100,List.of(new GenericIngredient(new AlloyStackIngredient(full).toVanilla(),1)),List.of(),List.of(new GenericOutput(empty)),List.of(new FluidStack(type,32000)),List.of(),"",FluidIconItem.make(type,32000),false,"",10001+i*2);put(recipes,"package/"+type.getID(),new AssemblerRecipe(into));put(recipes,"unpackage/"+type.getID(),new AssemblerRecipe(out));}}
    private static void writeTemplate(Path path,Collection<RecipeHolder<AssemblerRecipe>> recipes)throws java.io.IOException{var root=new JsonObject();root.addProperty("comment","改名为 hbmAssemblyMachine.json 后替换完整默认配方表。输入按位置匹配，流体池名称区分大小写。");var values=new JsonArray();for(var holder:recipes)try{values.add(GenericRecipeConfigCodec.write(holder.value().data()));}catch(IllegalArgumentException error){UNRESOLVED.add("template "+holder.id()+": "+error.getMessage());}root.add("recipes",values);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
}
