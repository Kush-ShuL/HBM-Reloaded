package com.hbm.machine.oilcatalyticcracker;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.recipe.generic.GenericRecipeData;
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
/** Original hbmCracking.json full replacement, last input-fluid definition wins; native recipes are genuinely synchronized. */
public final class CatalyticCrackerRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e)->install(e.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent e)->{var server=e.getPlayerList().getServer();if(e.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<CatalyticCrackerRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(CatalyticCrackerContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
 public static CatalyticCrackerRecipe find(RecipeManager manager,HbmFluidType input){var values=recipes(manager);for(int i=values.size()-1;i>=0;i--)if(values.get(i).inputFluids().getFirst().type==input)return values.get(i);return null;}public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
 private static HbmFluidType type(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unregistered original cracking fluid "+name);return type;}
 private static FluidStack fluid(JsonArray a){return new FluidStack(type(a.get(0).getAsString()),a.get(1).getAsInt(),a.size()>2?a.get(2).getAsInt():0);}
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var added=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmCracking.json");
  try{Files.createDirectories(directory);boolean custom=Files.isRegularFile(active);JsonObject root;if(custom){merged.values().removeIf(h->h.value().getType()==CatalyticCrackerContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{try(var stream=CatalyticCrackerRecipes.class.getResourceAsStream("/hbm/oil-catalytic-cracker-defaults.json")){if(stream==null)throw new IllegalStateException("Missing true12 cracking defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmCracking.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
   int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=type(row.get("input").getAsString());var data=new GenericRecipeData("cracking."+input.getName(),0,0,List.of(),List.of(new FluidStack(input,100),new FluidStack(HbmFluids.fromName("STEAM"),200)),List.of(),List.of(fluid(row.getAsJsonArray("output1")),fluid(row.getAsJsonArray("output2")),new FluidStack(HbmFluids.fromName("SPENTSTEAM"),2)),List.of(),"",ItemStack.EMPTY,false,"",order);added.values().removeIf(h->((CatalyticCrackerRecipe)h.value()).inputFluids().getFirst().type==input);var id=ResourceLocation.fromNamespaceAndPath("hbm","catalytic_cracking/"+(custom?"config/":"default/")+order);added.put(id,new RecipeHolder<>(id,new CatalyticCrackerRecipe(data)));}catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load actual catalytic cracking recipe replacement",error);}added.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,added);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().error("Unresolved original catalytic cracking recipes: {}",UNRESOLVED);
 }
 private CatalyticCrackerRecipes(){}
}
