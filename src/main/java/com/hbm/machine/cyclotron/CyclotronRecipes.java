package com.hbm.machine.cyclotron;
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
/** Actual hbmCyclotron.json replacement and the only RecipeManager-backed cyclotron table. */
public final class CyclotronRecipes {
 private record Key(String value,int sourceHash){@Override public int hashCode(){return sourceHash;}}
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
 private record Snapshot(List<RecipeHolder<CyclotronRecipe>> source,List<CyclotronRecipe> values){}
 private static final Map<RecipeManager,Snapshot> CACHE=new WeakHashMap<>();
 private static final List<String> UNRESOLVED=new ArrayList<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e)->install(e.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent e)->{var server=e.getPlayerList().getServer();if(e.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
 public static List<CyclotronRecipe> recipes(RecipeManager manager){var current=manager.getAllRecipesFor(CyclotronContent.TYPE.get());var cached=CACHE.get(manager);if(cached!=null&&cached.source().equals(current))return cached.values();var table=new HashMap<Key,CyclotronRecipe>();current.stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).forEach(r->table.put(new Key(r.sourceKey(),r.sourceHash()),r));var values=List.copyOf(table.values());CACHE.put(manager,new Snapshot(current,values));return values;}
 public static CyclotronRecipe find(RecipeManager manager,ItemStack particle,ItemStack target){if(particle.isEmpty()||target.isEmpty())return null;for(var recipe:recipes(manager))if(recipe.matches(particle,target))return recipe;return null;}
 /** Original getRecipes expands each target.extractForNEI(); an empty external dictionary yields no display. */
 public static List<CyclotronRecipe> visibleRecipes(RecipeManager manager){return recipes(manager).stream().filter(r->!r.inputItems().get(1).examples().isEmpty()).toList();}
 private static int count(JsonArray a,int index){return a.size()>index?a.get(index).getAsInt():1;}
 private static int meta(JsonArray a,int index){return a.size()>index?a.get(index).getAsInt():0;}
 private static int itemHash(String name,int meta,int count){return 31*(31*(31+name.hashCode())+meta)+count;}
 private static String particleKey(JsonArray a){return a.get(0).getAsString()+"@"+meta(a,2)+"x"+count(a,1);}
 private static String targetKey(JsonArray a){String kind=a.get(0).getAsString();return kind+":"+a.get(1).getAsString()+"@"+(kind.equals("dict")?0:meta(a,3))+"x"+count(a,2);}
 private static CyclotronRecipe read(JsonObject row,int order,Map<String,String> aliases){
  var particle=row.getAsJsonArray("particle");var target=row.getAsJsonArray("input");var part=GenericRecipeConfigCodec.readStack(particle,aliases);
  var particleSpec=new JsonArray();particleSpec.add("item");particleSpec.add(particle.get(0));particleSpec.add(count(particle,1));particleSpec.add(meta(particle,2));
  var output=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),aliases);int amount=row.get("antimatter").getAsInt();
  var data=new GenericRecipeData("cyclotron."+order,690,1000000,List.of(GenericRecipeConfigCodec.readIngredient(particleSpec,aliases),GenericRecipeConfigCodec.readIngredient(target,aliases)),List.of(),List.of(new GenericOutput(output)),List.of(new FluidStack(HbmFluids.fromName("AMAT"),amount)),List.of(),"",part.copyWithCount(1),false,"",order);
  int first=itemHash(particle.get(0).getAsString(),meta(particle,2),count(particle,1));String kind=target.get(0).getAsString();int second=kind.equals("dict")?31*(31+target.get(1).getAsString().hashCode())+count(target,2):itemHash(target.get(1).getAsString(),meta(target,3),count(target,2));
  return new CyclotronRecipe(data,particleKey(particle)+"|"+targetKey(target),27644437*(27644437+first)+second);
 }
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){
  var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
  var added=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=directory.resolve("hbmCyclotron.json");
  try{Files.createDirectories(directory);JsonObject defaults;try(var stream=CyclotronRecipes.class.getResourceAsStream("/hbm/cyclotron-defaults.json")){if(stream==null)throw new IllegalStateException("Missing actual42 cyclotron defaults");defaults=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}
   var aliases=new HashMap<String,String>();for(var entry:defaults.getAsJsonObject("aliases").entrySet())aliases.put(entry.getKey(),entry.getValue().getAsString());
   boolean custom=Files.isRegularFile(active);JsonObject root;if(custom){merged.values().removeIf(h->h.value().getType()==CyclotronContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{root=defaults;var template=root.deepCopy();template.remove("aliases");Files.writeString(directory.resolve("_hbmCyclotron.json"),new GsonBuilder().setPrettyPrinting().create().toJson(template)+"\n");}
   int order=0;for(var value:root.getAsJsonArray("recipes")){try{var recipe=read(value.getAsJsonObject(),order,aliases);var id=ResourceLocation.fromNamespaceAndPath("hbm","cyclotron/"+(custom?"config/":"default/")+order);added.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load actual cyclotron recipe replacement",error);}
  added.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,added);CACHE.remove(manager);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().error("Unresolved actual cyclotron recipes: {}",UNRESOLVED);
 }
 private CyclotronRecipes(){}
}
