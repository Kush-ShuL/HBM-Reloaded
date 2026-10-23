package com.hbm.machine.reactor.iter.breeder;
import com.google.gson.*;
import com.hbm.fluid.*;
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
/** Original hbmIrradiationFluids.json whole-map replacement and exact3 default native recipes, not another solid irradiation config. */
public final class FluidBreedingRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<FluidBreedingRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(FusionBreederContent.FLUID_TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(FluidBreedingRecipe::order)).toList();}
 public static FluidBreedingRecipe find(RecipeManager manager,HbmFluidType type){var values=recipes(manager);for(int i=values.size()-1;i>=0;i--)if(values.get(i).input().type==type)return values.get(i);return null;}
 private static FluidStack fluid(JsonArray array){String name=array.get(0).getAsString();var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unregistered irradiation fluid "+name);return new FluidStack(type,array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0);}
 private static JsonArray fluid(FluidStack stack){var array=new JsonArray();array.add(stack.type.getName());array.add(stack.fill);if(stack.pressure!=0)array.add(stack.pressure);return array;}
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();var dir=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=dir.resolve("hbmIrradiationFluids.json");try{Files.createDirectories(dir);if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==FusionBreederContent.FLUID_TYPE.get());JsonObject root;try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}int order=0;for(var element:root.getAsJsonArray("recipes")){try{var row=element.getAsJsonObject();var recipe=new FluidBreedingRecipe(fluid(row.getAsJsonArray("input")),fluid(row.getAsJsonArray("output")),order);additions.values().removeIf(holder->((FluidBreedingRecipe)holder.value()).input().type==recipe.input().type);var id=ResourceLocation.parse("hbm:fluid_breeding/config/"+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){LogUtils.getLogger().warn("Unbound original fluid irradiation custom row {}",order,error);}order++;}}else{var root=new JsonObject();var rows=new JsonArray();for(var recipe:recipes(manager)){var row=new JsonObject();row.add("input",fluid(recipe.input()));row.add("output",fluid(recipe.output()));rows.add(row);}root.add("recipes",rows);Files.writeString(dir.resolve("_hbmIrradiationFluids.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}}catch(Exception error){LogUtils.getLogger().error("Cannot load original fluid irradiation recipes",error);}additions.forEach(merged::put);if(!additions.isEmpty()||!previous.isEmpty()||Files.isRegularFile(active))manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);}
 private FluidBreedingRecipes(){}
}
