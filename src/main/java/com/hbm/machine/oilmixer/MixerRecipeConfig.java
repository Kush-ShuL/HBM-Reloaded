package com.hbm.machine.oilmixer;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Source hbmMixer.json is full replacement, including empty output groups and last duplicate group wins. */
public final class MixerRecipeConfig {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e)->install(e.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent e)->{var server=e.getPlayerList().getServer();if(e.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<MixerRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(MixerContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
 public static List<MixerRecipe> output(RecipeManager manager,HbmFluidType type){return recipes(manager).stream().filter(r->r.outputFluids().getFirst().type==type).toList();}
 private static HbmFluidType type(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown source mixer fluid "+name);return type;}
 private static FluidStack fluid(JsonArray a){return new FluidStack(type(a.get(0).getAsString()),a.get(1).getAsInt(),a.size()>2?a.get(2).getAsInt():0);}
 private static GenericIngredient solid(JsonArray a){String name=a.get(1).getAsString().replace("item.","").replace("tile.","");int count=a.size()>2?a.get(2).getAsInt():1,meta=a.size()>3?a.get(3).getAsInt():0;
  if(a.get(0).getAsString().equals("item")&&Set.of("fish","minecraft:fish").contains(name)){var fish=new ArrayList<Item>();if(meta==32767)fish.addAll(List.of(Items.COD,Items.SALMON,Items.TROPICAL_FISH,Items.PUFFERFISH));else if(meta>=0&&meta<4)fish.add(List.of(Items.COD,Items.SALMON,Items.TROPICAL_FISH,Items.PUFFERFISH).get(meta));else throw new IllegalArgumentException("Unknown old fish metadata "+meta);return new GenericIngredient(Ingredient.of(fish.toArray(ItemLike[]::new)),count);}
  if(a.get(0).getAsString().equals("item")&&Set.of("double_plant","minecraft:double_plant").contains(name)){var plants=List.of(Items.SUNFLOWER,Items.LILAC,Items.TALL_GRASS,Items.LARGE_FERN,Items.ROSE_BUSH,Items.PEONY);if(meta==32767)return new GenericIngredient(Ingredient.of(plants.toArray(ItemLike[]::new)),count);if(meta<0||meta>=plants.size())throw new IllegalArgumentException("Unknown old double_plant metadata "+meta);return new GenericIngredient(Ingredient.of(plants.get(meta)),count);}
  return GenericRecipeConfigCodec.readIngredient(a,Map.of());
 }
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var h:manager.getOrderedRecipes())if(previous.get(h.id())!=h)merged.put(h.id(),h);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();Path dir=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=dir.resolve("hbmMixer.json");
  try{Files.createDirectories(dir);boolean custom=Files.isRegularFile(active);JsonObject root;try(var stream=MixerRecipeConfig.class.getResourceAsStream("/hbm/oil-mixer-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing actual mixer source recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}if(custom){merged.values().removeIf(h->h.value().getType()==MixerContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else Files.writeString(dir.resolve("_hbmMixer.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
   int order=0;for(var groupValue:root.getAsJsonArray("recipes")){var group=groupValue.getAsJsonObject();var out=type(group.get("outputType").getAsString());additions.values().removeIf(h->((MixerRecipe)h.value()).outputFluids().getFirst().type==out);for(var value:group.getAsJsonArray("recipes")){int index=order++;try{var row=value.getAsJsonObject();var fluids=new ArrayList<FluidStack>();int mask=0;for(int i=0;i<2;i++)if(row.has("input"+(i+1))){mask|=1<<i;fluids.add(fluid(row.getAsJsonArray("input"+(i+1))));}var solids=row.has("solidInput")?List.of(solid(row.getAsJsonArray("solidInput"))):List.<GenericIngredient>of();var data=new GenericRecipeData("mixer."+out.getName(),row.get("duration").getAsInt(),50,solids,fluids,List.of(),List.of(new FluidStack(out,row.get("outputAmount").getAsInt())),List.of(),"",ItemStack.EMPTY,false,"",index);var id=ResourceLocation.fromNamespaceAndPath("hbm","mixer/"+(custom?"config/":"default/")+index);additions.put(id,new RecipeHolder<>(id,new MixerRecipe(data,mask)));}catch(Exception error){LogUtils.getLogger().error("Actual mixer recipe {} remains unresolved: {}",index,rowText(value),error);}}}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load actual mixer recipes",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);
 }
 private static String rowText(JsonElement element){return element.toString();}private MixerRecipeConfig(){}
}
