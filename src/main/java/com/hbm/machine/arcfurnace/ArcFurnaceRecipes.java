package com.hbm.machine.arcfurnace;
import com.google.gson.*;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.material.*;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.hbm.registry.HbmItemTags;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Source ArcFurnaceRecipes order/occupancy/cache, material and eligible-furnace autogen, and replacement config. */
public final class ArcFurnaceRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
 private static final Map<RecipeManager,Map<Key,ArcFurnaceRecipe>> SOLID=new WeakHashMap<>(),LIQUID=new WeakHashMap<>();
 private record Key(ResourceLocation item,int metadata){static Key of(ItemStack stack){return new Key(BuiltInRegistries.ITEM.getKey(stack.getItem()),HbmMaterials.metadata(stack));}}
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{if(event.getPlayer()==null||!INSTALLED.containsKey(event.getPlayerList().getServer().getRecipeManager()))install(event.getPlayerList().getServer());});}
 public static List<ArcFurnaceRecipe> all(RecipeManager manager){return manager.getAllRecipesFor(ArcFurnaceContent.RECIPE_TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(ArcFurnaceRecipe::order)).toList();}
 public static ArcFurnaceRecipe output(RecipeManager manager,ItemStack input,boolean liquid){
  if(input.isEmpty())return null;
  input=com.hbm.ore.RawOreContent.processingStack(input);
  if(liquid&&input.getItem() instanceof ScrapsItem){var mat=ScrapsItem.getMaterials(input);if(mat!=null&&mat.material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE)return new ArcFurnaceRecipe(Ingredient.of(input.copyWithCount(1)),ItemStack.EMPTY,List.of(mat),-1);}
  var cache=(liquid?LIQUID:SOLID).computeIfAbsent(manager,key->new HashMap<>());var key=Key.of(input);if(cache.containsKey(key))return cache.get(key);
  ArcFurnaceRecipe result=null;for(var recipe:all(manager))if(recipe.inMode(liquid)&&com.hbm.ore.OreRecipeInputs.test(recipe.input(),input)){result=recipe;break;}cache.put(key,result);return result;
 }
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){
  var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
  var recipes=new ArrayList<ArcFurnaceRecipe>();var occupiedSolid=new HashSet<Key>();var occupiedLiquid=new HashSet<Key>();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmArcFurnace.json");
  var unresolved=new ArrayList<String>();
  try{Files.createDirectories(directory);JsonObject source;
   if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==ArcFurnaceContent.RECIPE_TYPE.get());try(var reader=Files.newBufferedReader(active)){source=JsonParser.parseReader(reader).getAsJsonObject();}}
   else try(var stream=ArcFurnaceRecipes.class.getResourceAsStream("/hbm/arc-furnace-recipes.json")){if(stream==null)throw new IllegalStateException("Missing original arc furnace recipe data");source=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}
   int index=0;for(var entry:source.getAsJsonArray("recipes")){try{var row=entry.getAsJsonObject();var sourceInput=GenericRecipeConfigCodec.readIngredient(row.getAsJsonArray("input"),Map.of(),server.registryAccess());var input=sourceInput.ingredient();var solid=row.has("solid")?GenericRecipeConfigCodec.readStack(row.getAsJsonArray("solid"),Map.of()):ItemStack.EMPTY;var mats=new ArrayList<MaterialStack>();if(row.has("fluid"))for(var value:row.getAsJsonArray("fluid")){var array=value.getAsJsonArray();var material=HbmMaterials.byName(array.get(0).getAsString());if(material==null)throw new IllegalArgumentException("Unknown material "+array.get(0));if(material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE)mats.add(new MaterialStack(material,array.get(1).getAsInt()));}add(recipes,occupiedSolid,occupiedLiquid,new ArcFurnaceRecipe(input,sourceInput.count(),solid,mats,recipes.size()));}catch(Exception error){unresolved.add("source row "+index+": "+error.getMessage());}index++;}
   int explicitCount=recipes.size();
   if(!Files.isRegularFile(active)){
    for(var material:HbmMaterials.ordered())if(material.smeltsInto().smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE)for(var shape:HbmMaterials.shapes())if(!shape.noAutogen()){
      String tag=HbmItemTags.material(shape.symbol(),material.names().getFirst());
      if(HbmItemTags.items(tag).findAny().isPresent())add(recipes,occupiedSolid,occupiedLiquid,new ArcFurnaceRecipe(HbmItemTags.ingredient(tag),ItemStack.EMPTY,List.of(new MaterialStack(material.smeltsInto(),shape.q(1)*material.convOut()/material.convIn())),recipes.size()));
    }
    for(var entry:HbmMaterials.tagDistributions()){var mats=smeltable(entry.materials());if(!mats.isEmpty())add(recipes,occupiedSolid,occupiedLiquid,new ArcFurnaceRecipe(HbmItemTags.ingredient(entry.tag()),ItemStack.EMPTY,mats,recipes.size()));}
    for(var entry:HbmMaterials.stackDistributions()){var mats=smeltable(entry.materials());if(!mats.isEmpty()&&BuiltInRegistries.ITEM.containsKey(entry.item()))add(recipes,occupiedSolid,occupiedLiquid,new ArcFurnaceRecipe(new AlloyStackIngredient(MaterialDistributionConfig.legacyStack(entry.item().toString(),entry.metadata())).toVanilla(),ItemStack.EMPTY,mats,recipes.size()));}


   }
   for(var holder:manager.getAllRecipesFor(RecipeType.SMELTING)){var result=holder.value().getResultItem(server.registryAccess());if(result.isEmpty())continue;for(var ingredient:holder.value().getIngredients())for(var input:ingredient.getItems())if(arcSmeltable(input)||arcSmeltable(result))add(recipes,occupiedSolid,occupiedLiquid,new ArcFurnaceRecipe(new AlloyStackIngredient(input.copyWithCount(1)).toVanilla(),result,List.of(),recipes.size()));}
   if(!Files.isRegularFile(active)){JsonObject exported=new JsonObject();var array=source.getAsJsonArray("recipes").deepCopy();for(int i=explicitCount;i<recipes.size();i++)array.add(write(recipes.get(i),server.registryAccess()));exported.add("recipes",array);Files.writeString(directory.resolve("_hbmArcFurnace.json"),new GsonBuilder().setPrettyPrinting().create().toJson(exported)+"\n");}
  }catch(Exception error){LogUtils.getLogger().error("Cannot load source arc furnace recipes",error);}
  var installed=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var recipe:recipes){var id=ResourceLocation.fromNamespaceAndPath("hbm","arc_furnace/"+(Files.isRegularFile(active)?"config/":"default/")+recipe.order());installed.put(id,new RecipeHolder<>(id,recipe));}installed.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,installed);SOLID.remove(manager);LIQUID.remove(manager);if(!unresolved.isEmpty())LogUtils.getLogger().warn("Arc furnace retains {} source recipes unbound to actual registered dependencies: {}",unresolved.size(),String.join("; ",unresolved));
 }
 private static List<MaterialStack> smeltable(List<MaterialStack> input){return input.stream().filter(mat->mat.material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE).map(MaterialStack::copy).toList();}
 private static void add(List<ArcFurnaceRecipe> recipes,Set<Key> solid,Set<Key> liquid,ArcFurnaceRecipe recipe){var examples=Arrays.stream(recipe.input().getItems()).map(stack->{var key=Key.of(stack);return key.metadata==32767?new Key(key.item,0):key;}).toList();for(var key:examples)if(!recipe.solid().isEmpty()&&solid.contains(key)||!recipe.molten().isEmpty()&&liquid.contains(key))return;recipes.add(recipe);if(!recipe.solid().isEmpty())solid.addAll(examples);if(!recipe.molten().isEmpty())liquid.addAll(examples);}
 private static boolean arcSmeltable(ItemStack stack){String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();if(Set.of("minecraft:gold_ore","minecraft:iron_ore","minecraft:lapis_ore","minecraft:diamond_ore","minecraft:redstone_ore","minecraft:emerald_ore","minecraft:nether_quartz_ore","minecraft:gold_block","minecraft:iron_block","minecraft:lapis_block","minecraft:diamond_block","minecraft:redstone_block","minecraft:emerald_block","minecraft:quartz_block","minecraft:iron_ingot","minecraft:gold_ingot","minecraft:brick","minecraft:nether_brick").contains(id))return true;return HbmItemTags.tags(stack).stream().anyMatch(ArcFurnaceRecipes::arcCategory);}
 private static boolean arcCategory(String tag){String category=HbmItemTags.category(tag);if(tag.startsWith("hbm:material_groups/")){String path=ResourceLocation.parse(tag).getPath();int split=path.indexOf('/',"material_groups/".length());if(split>=0)category=path.substring(split+1);}return category.equals("ingots")||category.equals("ores")||category.startsWith("ores_in_ground/")||category.equals("plates")||category.startsWith("plates/")||category.equals("storage_blocks")||category.equals("glass_blocks");}
 private static JsonObject write(ArcFurnaceRecipe recipe,net.minecraft.core.HolderLookup.Provider lookup){var row=new JsonObject();row.add("input",GenericRecipeConfigCodec.writeIngredient(recipe.input(),recipe.inputCount(),lookup));if(!recipe.solid().isEmpty())row.add("solid",GenericRecipeConfigCodec.writeStack(recipe.solid()));if(!recipe.molten().isEmpty()){var fluid=new JsonArray();for(var mat:recipe.molten()){var a=new JsonArray();a.add(mat.material.names().getFirst());a.add(mat.amount);fluid.add(a);}row.add("fluid",fluid);}return row;}
 private ArcFurnaceRecipes(){}
}
