package com.hbm.machine.compressor;
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
/** Original hbmCompressor.json replacement syntax; source readRecipe intentionally defaults duration to100. */
public final class CompressorRecipeConfig {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<CompressorRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(CompressorContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
 public static CompressorRecipe find(RecipeManager manager,HbmFluidType type,int pressure){return recipes(manager).stream().filter(r->{var in=r.inputFluids().getFirst();return in.type==type&&in.pressure==pressure;}).reduce((a,b)->b).orElse(null);}
 private static FluidStack fluid(JsonArray array){String name=array.get(0).getAsString();var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown compressor fluid "+name);return new FluidStack(type,array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0);}
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var h:manager.getOrderedRecipes())if(previous.get(h.id())!=h)merged.put(h.id(),h);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmCompressor.json");try{Files.createDirectories(directory);boolean custom=Files.isRegularFile(active);JsonObject root;try(var stream=CompressorRecipeConfig.class.getResourceAsStream("/hbm/compressor-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source compressor recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}if(custom){merged.values().removeIf(h->h.value().getType()==CompressorContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{JsonObject exported=root.deepCopy();for(var row:exported.getAsJsonArray("recipes"))row.getAsJsonObject().remove("duration");Files.writeString(directory.resolve("_hbmCompressor.json"),new GsonBuilder().setPrettyPrinting().create().toJson(exported)+"\n");}int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=fluid(row.getAsJsonArray("input"));var output=fluid(row.getAsJsonArray("output"));int duration=custom?100:row.get("duration").getAsInt();var data=new GenericRecipeData("compressor."+input.type.getName()+"."+input.pressure,duration,2500,List.of(),List.of(input),List.of(),List.of(output),List.of(),"",ItemStack.EMPTY,false,"",order);var id=ResourceLocation.fromNamespaceAndPath("hbm","compressor/"+(custom?"config/":"default/")+order);additions.values().removeIf(h->{var old=((CompressorRecipe)h.value()).inputFluids().getFirst();return old.type==input.type&&old.pressure==input.pressure;});additions.put(id,new RecipeHolder<>(id,new CompressorRecipe(data)));}catch(Exception error){LogUtils.getLogger().error("Invalid source compressor row {}",order,error);}order++;}}catch(Exception error){LogUtils.getLogger().error("Cannot load actual compressor recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);}
 private CompressorRecipeConfig(){}
}
