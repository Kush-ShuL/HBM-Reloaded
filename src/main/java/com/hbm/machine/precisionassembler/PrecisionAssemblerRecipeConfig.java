package com.hbm.machine.precisionassembler;
import com.google.gson.*;
import com.hbm.fluid.FluidStack;
import com.hbm.machine.centrifuge.CentrifugeNbtIngredient;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Original hbmPrecisionAssembly.json and source mode defaults; registerPair resolves the actual first live OreDictionary choice. */
public final class PrecisionAssemblerRecipeConfig {
 private static final Map<String,String>ALIASES=new LinkedHashMap<>();private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>>INSTALLED=new WeakHashMap<>();private static final List<String>UNRESOLVED=new ArrayList<>();
 public static Map<String,String>aliases(){return Collections.unmodifiableMap(ALIASES);}
 public static String sourceName(Item item){String actual=BuiltInRegistries.ITEM.getKey(item).toString();for(var entry:ALIASES.entrySet())if(entry.getValue().equals(actual)&&(entry.getKey().startsWith("hbm:item.")||entry.getKey().startsWith("hbm:tile.")))return entry.getKey();return actual;}
 public static List<String>unresolved(){return List.copyOf(UNRESOLVED);}
 public static void register(){try(var stream=PrecisionAssemblerRecipeConfig.class.getResourceAsStream("/hbm/precision-assembler-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing original precision item aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 static void setup(FMLCommonSetupEvent event){}
 public static List<PrecisionAssemblerRecipe>recipes(RecipeManager manager){return manager.getAllRecipesFor(PrecisionAssemblerContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(value->value.data().order())).toList();}
 private static GenericRecipeData data(GenericRecipeData source,String name,List<GenericIngredient>in,List<FluidStack>fluids,List<GenericOutput>out,List<FluidStack>outputs,List<String>pools,ItemStack icon,String wrapper,int order){return new GenericRecipeData(name,source.duration(),source.power(),in,fluids,out,outputs,pools,source.group(),icon,source.named(),wrapper,order);}
 private static void add(Map<ResourceLocation,RecipeHolder<?>>result,String suffix,GenericRecipeData data){var id=ResourceLocation.fromNamespaceAndPath("hbm","precision_assembly/"+suffix);result.put(id,new RecipeHolder<>(id,new PrecisionAssemblerRecipe(data)));}
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=directory.resolve("hbmPrecisionAssembly.json");
  try{Files.createDirectories(directory);JsonArray rows;boolean custom=Files.isRegularFile(active);if(custom){merged.values().removeIf(holder->holder.value().getType()==PrecisionAssemblerContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){rows=JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("recipes");}}
   else{String mode=(com.hbm.config.GeneralModes.mode528()?"528":"normal")+(com.hbm.config.GeneralModes.expensive()?"_expensive":"");try(var stream=PrecisionAssemblerRecipeConfig.class.getResourceAsStream("/hbm/precision-assembler-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing full original precision recipes");rows=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject(mode).getAsJsonArray("rows");}}
   var names=new HashSet<String>();int order=0;for(var value:rows){var row=value.getAsJsonObject();String label=custom?"config row"+order:row.getAsJsonObject("recipe").get("name").getAsString();try{var original=GenericRecipeConfigCodec.read(custom?row:row.getAsJsonObject("recipe"),server.registryAccess(),ALIASES);if(original.inputItems().size()>9||original.outputItems().size()>9||original.inputFluids().size()>1||original.outputFluids().size()>1)throw new IllegalArgumentException("Original precision machine limits are9/1/9/1");if(!names.add(original.name()))throw new IllegalArgumentException("Duplicate source recipe "+original.name());
     if(!custom&&row.get("kind").getAsString().equals("pair")){ItemStack target=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),ALIASES),broken=BrokenItem.make(target);int chance=row.get("chance").getAsInt();float reclaim=row.get("reclaim").getAsInt()/100F;
      // Resolve all first-stack recovery views BEFORE publishing either member of the genuine pair.
      var recovered=new ArrayList<GenericOutput>();for(var input:original.inputItems()){var examples=input.examples();if(examples.isEmpty())throw new IllegalArgumentException("Source registerPair has no first OreDictionary example");recovered.add(new GenericOutput(List.of(new GenericOutput.Chance(examples.getFirst().copy(),reclaim,0)),false));}
      var recoveredFluid=new ArrayList<FluidStack>();if(!original.inputFluids().isEmpty()){var fluid=original.inputFluids().getFirst();recoveredFluid.add(new FluidStack(fluid.type,Math.round(fluid.fill*reclaim),fluid.pressure));}
      var production=new GenericOutput(List.of(new GenericOutput.Chance(target,1F,chance),new GenericOutput.Chance(broken,1F,100-chance)),true);
      var input=new GenericIngredient(new CentrifugeNbtIngredient(new ItemStack(PrecisionAssemblerContent.BROKEN.get()),BrokenItem.sourceData(broken)).toVanilla(),broken.getCount());
      add(additions,"default/"+order+"/make",data(original,original.name(),original.inputItems(),original.inputFluids(),List.of(production),List.of(),original.pools(),original.icon(),original.nameWrapper(),order*2));
      add(additions,"default/"+order+"/recycle",data(original,original.name()+".recycle",List.of(input),List.of(),recovered,recoveredFluid,List.of(),broken,"precass.recycle",order*2+1));
     }else add(additions,(custom?"config/":"default/")+order,data(original,original.name(),original.inputItems(),original.inputFluids(),original.outputItems(),original.outputFluids(),original.pools(),original.icon(),original.nameWrapper(),order*2));
    }catch(Exception error){UNRESOLVED.add(label+": "+error.getMessage());}order++;}
   var exported=new JsonObject();exported.addProperty("comment","改名为 hbmPrecisionAssembly.json 后替换完整默认配方表。次品按权重产出，回收概率对每个物品独立计算。");var recipes=new JsonArray();for(var holder:additions.values())recipes.add(GenericRecipeConfigCodec.write(((PrecisionAssemblerRecipe)holder.value()).data()));exported.add("recipes",recipes);Files.writeString(directory.resolve("_hbmPrecisionAssembly.json"),new GsonBuilder().setPrettyPrinting().create().toJson(exported)+"\n");
  }catch(Exception error){LogUtils.getLogger().error("Cannot load original precision assembler recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);Blueprints.registerRecipes(ResourceLocation.fromNamespaceAndPath("hbm","precision_assembly"),recipes(manager));for(var error:UNRESOLVED)LogUtils.getLogger().warn("Unbound precision assembler recipe: {}",error);
 }
 private PrecisionAssemblerRecipeConfig(){}
}
