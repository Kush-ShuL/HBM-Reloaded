package com.hbm.machine.pyrooven;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Real ordered71 operations, runtime Flammable auto quantities and hbmPyrolysis.json full replacement. */
public final class PyroOvenRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();private static final Map<String,String> ALIASES=new LinkedHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
 public static void register(){try(var stream=PyroOvenRecipes.class.getResourceAsStream("/hbm/pyro-oven-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing original pyrolysis item aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(e->ALIASES.put(e.getKey(),e.getValue().getAsString()));}catch(Exception error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<PyroOvenRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(PyroOvenContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var h:manager.getOrderedRecipes())if(previous.get(h.id())!=h)merged.put(h.id(),h);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path dir=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=dir.resolve("hbmPyrolysis.json");boolean custom=Files.isRegularFile(active);
  try{Files.createDirectories(dir);JsonObject root;if(custom){merged.values().removeIf(h->h.value().getType()==PyroOvenContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{try(var stream=PyroOvenRecipes.class.getResourceAsStream("/hbm/pyro-oven-defaults.json")){if(stream==null)throw new IllegalStateException("Missing actual71 pyrolysis defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}}
   int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();if(!custom&&row.has("autoFluid")){var input=type(row.get("autoFluid").getAsString());var trait=input.getTrait(FluidTraits.Flammable.class);if(trait==null)throw new IllegalArgumentException("Missing original Flammable trait "+input.getName());long heat=trait.getHeatEnergy();int amount=(int)(row.get("autoTU").getAsLong()*1000L*.5D/heat);if(amount>10000)amount-=amount%1000;else if(amount>1000)amount-=amount%100;else if(amount>100)amount-=amount%10;row=row.deepCopy();row.add("inputFluid",fluidJson(new FluidStack(input,Math.max(amount,1))));}
    var inputs=row.has("inputItem")?List.of(GenericRecipeConfigCodec.readIngredient(row.getAsJsonArray("inputItem"),ALIASES,server.registryAccess())):List.<GenericIngredient>of();var inputFluid=row.has("inputFluid")?List.of(fluid(row.getAsJsonArray("inputFluid"))):List.<FluidStack>of();var output=row.has("outputItem")?GenericRecipeConfigCodec.readStack(row.getAsJsonArray("outputItem"),ALIASES):ItemStack.EMPTY;var outputs=row.has("outputFluid")?List.of(fluid(row.getAsJsonArray("outputFluid"))):List.<FluidStack>of();var data=new GenericRecipeData("pyrooven.source."+order,row.get("duration").getAsInt(),10000,inputs,inputFluid,output.isEmpty()?List.of():List.of(new GenericOutput(output)),outputs,List.of(),"",ItemStack.EMPTY,false,"",order);var recipe=new PyroOvenRecipe(data);var id=ResourceLocation.parse("hbm:pyrooven/"+(custom?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));
   }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
   if(!custom)template(dir.resolve("_hbmPyrolysis.json"),additions.values().stream().map(h->(PyroOvenRecipe)h.value()).toList(),server.registryAccess());
  }catch(Exception error){LogUtils.getLogger().error("Cannot load actual pyrolysis source recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().error("Actual PyroOven recipes unresolved: {}",UNRESOLVED);
 }
 private static HbmFluidType type(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unregistered original pyrolysis fluid "+name);return type;}private static FluidStack fluid(JsonArray a){return new FluidStack(type(a.get(0).getAsString()),a.get(1).getAsInt(),a.size()>2?a.get(2).getAsInt():0);}
 private static JsonArray fluidJson(FluidStack f){var a=new JsonArray();a.add(f.type.getName());a.add(f.fill);if(f.pressure!=0)a.add(f.pressure);return a;}
 private static void template(Path path,List<PyroOvenRecipe> recipes,net.minecraft.core.HolderLookup.Provider lookup)throws Exception{var root=new JsonObject();var rows=new JsonArray();for(var recipe:recipes){var generic=GenericRecipeConfigCodec.write(recipe.data(),lookup);var row=new JsonObject();row.addProperty("duration",recipe.duration());if(recipe.inputItem()!=null)row.add("inputItem",generic.getAsJsonArray("inputItem").get(0));if(recipe.inputFluid()!=null)row.add("inputFluid",fluidJson(recipe.inputFluid()));if(!recipe.outputItem().isEmpty())row.add("outputItem",GenericRecipeConfigCodec.writeStack(recipe.outputItem()));if(recipe.outputFluid()!=null)row.add("outputFluid",fluidJson(recipe.outputFluid()));rows.add(row);}root.add("recipes",rows);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
 private PyroOvenRecipes(){}
}
