package com.hbm.machine.foundry.crucible;
import com.google.gson.*;
import com.hbm.material.*;
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
/** Original hbmCrucible.json complete-list replacement grammar, material names and source order. */
public final class CrucibleRecipeConfig {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{if(event.getPlayer()==null||!INSTALLED.containsKey(event.getPlayerList().getServer().getRecipeManager()))install(event.getPlayerList().getServer());});}
 public static List<CrucibleRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(CrucibleContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
 private static List<MaterialStack> materials(JsonArray entries){var list=new ArrayList<MaterialStack>();for(var entry:entries){var a=entry.getAsJsonArray();var material=HbmMaterials.byName(a.get(0).getAsString());if(material==null)throw new IllegalArgumentException("Unknown source material "+a.get(0));list.add(new MaterialStack(material,a.get(1).getAsInt()));}return list;}
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmCrucible.json");
  try{Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==CrucibleContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
   else{try(var stream=CrucibleRecipeConfig.class.getResourceAsStream("/hbm/crucible-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source crucible recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmCrucible.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
   int order=0;var names=new HashSet<String>();for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();String name=row.get("name").getAsString();if(!names.add(name))throw new IllegalArgumentException("Duplicate recipe "+name);int frequency=row.get("frequency").getAsInt();if(frequency<1)throw new IllegalArgumentException("frequency must be at least1");var icon=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("icon"),Map.of());var data=new GenericRecipeData(name,0,0,List.of(),List.of(),List.of(),List.of(),List.of(),"",icon,true,"",order);var recipe=new CrucibleRecipe(data,frequency,materials(row.getAsJsonArray("input")),materials(row.getAsJsonArray("output")));var id=ResourceLocation.fromNamespaceAndPath("hbm","crucible/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load source crucible alloys",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);UNRESOLVED.forEach(error->LogUtils.getLogger().warn("Unbound crucible recipe: {}",error));
 }
 private CrucibleRecipeConfig(){}
}
