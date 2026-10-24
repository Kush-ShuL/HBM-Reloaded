package com.hbm.machine.oilliquefactor;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.recipe.generic.GenericIngredient;
import com.hbm.registry.HbmOreDictionary;
import com.hbm.world.structure.loot.LegacyStructureItems;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
/** Real source34defaults/replacement config plus unconditional dynamic ItemFood fallback. No separate viewer recipe table. */
public final class LiquefactionRecipes {
 private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();private record View(Collection<RecipeHolder<?>> snapshot,List<LiquefactorRecipe> values){}private static final Map<RecipeManager,View> VIEWS=new WeakHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
 public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e)->install(e.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent e)->{var server=e.getPlayerList().getServer();if(e.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
 public static List<LiquefactorRecipe> recipes(RecipeManager manager){var snapshot=manager.getOrderedRecipes();synchronized(VIEWS){var old=VIEWS.get(manager);if(old!=null&&old.snapshot==snapshot)return old.values;var values=manager.getAllRecipesFor(LiquefactorContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(LiquefactorRecipe::order)).toList();VIEWS.put(manager,new View(snapshot,values));return values;}}
 public static FluidStack output(RecipeManager manager,ItemStack stack){if(stack.isEmpty())return null;var rows=recipes(manager);for(String kind:List.of("item","wildcard"))for(int i=rows.size()-1;i>=0;i--){var r=rows.get(i);if(r.keyKind().equals(kind)&&r.input().matches(stack,true))return r.output();}for(String tag:HbmOreDictionary.tags(stack))for(int i=rows.size()-1;i>=0;i--){var r=rows.get(i);if(r.keyKind().equals("dict")&&r.sourceKey().equals(tag))return r.output();}
  // 1.21 FoodProperties stores nutrition*sourceSaturationModifier*2 already.
  // Multiplying that saturation by10 preserves the source (nutrition*modifier)*20 amount.
  var food=stack.get(DataComponents.FOOD);return food==null?null:new FluidStack(HbmFluids.fromName("SALIENT"),(int)(food.saturation()*10F));
 }
 public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
 private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var r:manager.getOrderedRecipes())if(previous.get(r.id())!=r)merged.put(r.id(),r);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=directory.resolve("hbmLiquefactor.json");try{Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(r->r.value().getType()==LiquefactorContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{try(var stream=LiquefactionRecipes.class.getResourceAsStream("/hbm/oil-liquefactor-defaults.json")){if(stream==null)throw new IllegalStateException("Missing genuine liquefaction defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmLiquefactor.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
  int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var recipe=read(row,order,server.registryAccess());String identity=recipe.keyKind()+"/"+recipe.sourceKey();additions.values().removeIf(old->old.value() instanceof LiquefactorRecipe r&&(r.keyKind()+"/"+r.sourceKey()).equals(identity));var id=ResourceLocation.parse("hbm:oil_liquefaction/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
 }catch(Exception error){LogUtils.getLogger().error("Cannot load original hbmLiquefactor.json",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Unbound genuine liquefactor rows: {}",UNRESOLVED);}
 private static LiquefactorRecipe read(JsonObject row,int order,HolderLookup.Provider lookup){var in=row.getAsJsonArray("input");String kind=in.get(0).getAsString(),key;Ingredient ingredient;if(kind.equals("dict")){key=HbmOreDictionary.registerName(in.get(1).getAsString());ingredient=new LegacyOreIngredient(ResourceLocation.parse(key)).toVanilla();}else{String source=in.get(1).getAsString();int meta=!kind.equals("nbt")&&in.size()>3?in.get(3).getAsInt():0;
   if(meta==32767&&source.equals("minecraft:fish")){kind="wildcard";key="minecraft:fish@32767";ingredient=Ingredient.of(Items.COD,Items.SALMON,Items.TROPICAL_FISH,Items.PUFFERFISH);}else{var tag=new CompoundTag();tag.putString("id",source);tag.putByte("Count",(byte)1);tag.putShort("Damage",(short)(meta==32767?0:meta));var example=LegacyStructureItems.convertStack(tag,Map.of(),lookup);if(meta==32767){example.set(HbmHazards.LEGACY_METADATA.get(),32767);kind="wildcard";}key=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(example.getItem())+"@"+meta;
    // Source NBTStack is a different getClass() from the queried ComparableStack;
    // keep it serialized/viewable but unreachable exactly as in X5846.
    ingredient=new AlloyStackIngredient(example).toVanilla();}
  }var out=row.getAsJsonArray("output");String name=out.get(0).getAsString();var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown real fluid "+name);return new LiquefactorRecipe(new GenericIngredient(ingredient,1),new FluidStack(type,out.get(1).getAsInt(),out.size()>2?out.get(2).getAsInt():0),kind,key,in.toString(),order);}
 private LiquefactionRecipes(){}
}
