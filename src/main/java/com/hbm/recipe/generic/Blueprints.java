package com.hbm.recipe.generic;
import com.google.gson.*;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Shared ItemBlueprints + GenericRecipes global pool, retaining literal source pool names. */
public final class Blueprints {
    public static final String ALT="alt.",DISCOVER="discover.",SECRET="secret.",FIVE_TWO_EIGHT="528.";
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<BlueprintItem> ITEM=ITEMS.register("blueprints",BlueprintItem::new);
    public static final DeferredItem<BlueprintFolderItem> FOLDER=ITEMS.register("blueprint_folder",BlueprintFolderItem::new);
    private static final Map<ResourceLocation,List<? extends GenericMachineRecipe>> SETS=new LinkedHashMap<>();
    private static final Map<String,List<String>> DEFAULT_POOLS=new LinkedHashMap<>();
    private static final Map<String,String> OWNERS=new LinkedHashMap<>();private static final Map<ResourceLocation,String> SOURCES=new LinkedHashMap<>();
    private static boolean loaded,registered;
    private Blueprints(){}
    public static synchronized void register(IEventBus bus){if(registered)return;registered=true;ITEMS.register(bus);defaults();net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.bus.api.EventPriority.HIGHEST,(net.neoforged.neoforge.event.server.ServerAboutToStartEvent event)->clearRecipes());}
    private static synchronized void defaults(){if(loaded)return;loaded=true;try(var stream=Blueprints.class.getResourceAsStream("/hbm/blueprints.json")){if(stream!=null){var root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();root.getAsJsonObject("pools").entrySet().forEach(entry->{var names=new ArrayList<String>();entry.getValue().getAsJsonArray().forEach(name->names.add(name.getAsString()));DEFAULT_POOLS.put(entry.getKey(),names);});if(root.has("owners"))root.getAsJsonObject("owners").entrySet().forEach(entry->OWNERS.put(entry.getKey(),entry.getValue().getAsString()));}}catch(java.io.IOException error){throw new IllegalStateException(error);}}
    public static synchronized void declareRecipeSet(ResourceLocation type,String originalClass){SOURCES.put(type,originalClass);}
    public static synchronized void clearRecipes(){SETS.clear();}
    public static synchronized void registerRecipes(ResourceLocation type,List<? extends GenericMachineRecipe> recipes){SETS.put(type,List.copyOf(recipes));}
    public static synchronized Map<String,List<String>> pools(){defaults();Map<String,List<String>> pools=new LinkedHashMap<>();var boundSources=SETS.keySet().stream().map(SOURCES::get).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());DEFAULT_POOLS.forEach((key,value)->{var retained=value.stream().filter(name->!boundSources.contains(OWNERS.get(name))).toList();if(!retained.isEmpty())pools.put(key,new ArrayList<>(retained));});for(var recipes:SETS.values())for(var recipe:recipes)for(String pool:recipe.pools())pools.computeIfAbsent(pool,k->new ArrayList<>()).add(recipe.name());pools.replaceAll((key,value)->value.stream().distinct().toList());return Collections.unmodifiableMap(pools);}
    public static synchronized GenericMachineRecipe recipe(String name){GenericMachineRecipe result=null;for(var recipes:SETS.values())for(var recipe:recipes)if(recipe.name().equals(name))result=recipe;return result;}
    public static String pool(ItemStack stack){if(stack.isEmpty()||stack.getItem()!=ITEM.get()||!stack.has(DataComponents.CUSTOM_DATA))return null;var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();return tag.contains("pool")?tag.getString("pool"):null;}
    public static ItemStack make(String pool){var stack=new ItemStack(ITEM.get());CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putString("pool",pool));return stack;}
    public static int metadata(ItemStack stack){return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static List<ItemStack> creativeStacks(){var stacks=new ArrayList<ItemStack>();pools().keySet().stream().filter(pool->!pool.startsWith(SECRET)).map(Blueprints::make).forEach(stacks::add);for(int i=0;i<2;i++){var stack=new ItemStack(FOLDER.get());stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),i);stacks.add(stack);}return stacks;}
    public static int textureKind(ItemStack stack){String pool=pool(stack);return pool==null?0:pool.startsWith(DISCOVER)?1:pool.startsWith(SECRET)?2:pool.startsWith(FIVE_TWO_EIGHT)?3:0;}
}
