package com.hbm.machine.oilhydrotreater;
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
/** Actual hbmHydrotreating.json replacement, preserving per-100mB amounts/pressures and last-key-wins. */
public final class HydrotreaterRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
 private HydrotreaterRecipes(){}
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<HydrotreaterRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(HydrotreaterContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
 public static HydrotreaterRecipe find(RecipeManager manager,HbmFluidType type){return recipes(manager).stream().filter(r->r.inputFluids().getFirst().type==type).findFirst().orElse(null);}
 public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){
  var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
  var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmHydrotreating.json");
  try{Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==HydrotreaterContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
   else{try(var stream=HydrotreaterRecipes.class.getResourceAsStream("/hbm/oil-hydrotreater-defaults.json")){if(stream==null)throw new IllegalStateException("Missing original hydrotreating defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmHydrotreating.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
   int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=fluid(row.get("input").getAsString());var hydrogen=stack(row.getAsJsonArray("hydrogen"));var outputs=List.of(stack(row.getAsJsonArray("output1")),stack(row.getAsJsonArray("output2")));
     var data=new GenericRecipeData("hydrotreating."+input.getName(),2,10000,List.of(),List.of(new FluidStack(input,100),hydrogen),List.of(),outputs,List.of(),"",ItemStack.EMPTY,false,"",order);var id=ResourceLocation.fromNamespaceAndPath("hbm","hydrotreating/"+(Files.isRegularFile(active)?"config/":"default/")+order);
     additions.values().removeIf(holder->holder.value() instanceof HydrotreaterRecipe old&&old.inputFluids().getFirst().type==input);additions.put(id,new RecipeHolder<>(id,new HydrotreaterRecipe(data)));
    }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load original hydrotreating recipes",error);}
  additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Unresolved original hydrotreating recipes: {}",UNRESOLVED);
 }
 private static FluidStack stack(JsonArray array){return new FluidStack(fluid(array.get(0).getAsString()),array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0);}
 private static HbmFluidType fluid(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown original hydrotreating fluid "+name);return type;}
}
