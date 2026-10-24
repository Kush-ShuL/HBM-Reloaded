package com.hbm.machine.pa.recipe;
import com.google.gson.*;
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
/** Actual hbmParticleAccelerator.json whole-table replacement using the sole canonical SerializableRecipe array grammar. */
public final class PaRecipes{
    private static final Map<String,String> ALIASES=new HashMap<>();private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private record View(Collection<RecipeHolder<?>> snapshot,List<PaRecipe> recipes){}private static final Map<RecipeManager,View> VIEWS=new WeakHashMap<>();
    public static void register(){try(var stream=PaRecipes.class.getResourceAsStream("/hbm/particle-accelerator-item-aliases.json")){if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(e->ALIASES.put(e.getKey(),e.getValue().getAsString()));}catch(Exception error){throw new IllegalStateException(error);}NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent e)->install(e.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent e)->{var server=e.getPlayerList().getServer();if(e.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<PaRecipe> recipes(RecipeManager manager){var snapshot=manager.getOrderedRecipes();synchronized(VIEWS){var before=VIEWS.get(manager);if(before!=null&&before.snapshot==snapshot)return before.recipes;var list=manager.getAllRecipesFor(PaRecipeContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(PaRecipe::order)).toList();VIEWS.put(manager,new View(snapshot,list));return list;}}
    public static PaRecipe find(RecipeManager manager,ItemStack one,ItemStack two){for(var recipe:recipes(manager))if(recipe.matches(one,two))return recipe;return null;}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var added=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmParticleAccelerator.json");try{Files.createDirectories(directory);if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==PaRecipeContent.TYPE.get());JsonObject root;try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}int index=0;for(var value:root.getAsJsonArray("recipes")){try{var recipe=read(value.getAsJsonObject(),index);var id=ResourceLocation.parse("hbm:particle_accelerator/config/"+index);added.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){LogUtils.getLogger().warn("Unbound actual particle accelerator custom recipe {}",index,error);}index++;}}else template(directory.resolve("_hbmParticleAccelerator.json"),recipes(manager));}catch(Exception error){LogUtils.getLogger().error("Cannot load original particle accelerator replacement recipes",error);}added.forEach(merged::put);if(!added.isEmpty()||!previous.isEmpty()||Files.isRegularFile(active))manager.replaceRecipes(merged.values());INSTALLED.put(manager,added);}
    private static PaRecipe read(JsonObject row,int order){var inputs=row.getAsJsonArray("inputs");var outputs=row.getAsJsonArray("outputs");if(inputs.size()!=2||outputs.isEmpty()||outputs.size()>2)throw new IllegalArgumentException("Source PA collision requires two input stacks and one/two outputs");return new PaRecipe(GenericRecipeConfigCodec.readIngredient(inputs.get(0).getAsJsonArray(),ALIASES),GenericRecipeConfigCodec.readIngredient(inputs.get(1).getAsJsonArray(),ALIASES),row.get("momentum").getAsInt(),GenericRecipeConfigCodec.readStack(outputs.get(0).getAsJsonArray(),ALIASES),outputs.size()>1?Optional.of(GenericRecipeConfigCodec.readStack(outputs.get(1).getAsJsonArray(),ALIASES)):Optional.empty(),order);}
    private static void template(Path path,List<PaRecipe> recipes)throws Exception{var root=new JsonObject();var values=new JsonArray();for(var recipe:recipes){var row=new JsonObject();row.addProperty("momentum",recipe.momentum());var generic=new GenericRecipeData("",0,0,List.of(recipe.input1(),recipe.input2()),List.of(),List.of(),List.of(),List.of(),"",ItemStack.EMPTY,false,"",0);row.add("inputs",GenericRecipeConfigCodec.write(generic).get("inputItem"));var outputs=new JsonArray();outputs.add(GenericRecipeConfigCodec.writeStack(recipe.output1()));recipe.output2().ifPresent(stack->outputs.add(GenericRecipeConfigCodec.writeStack(stack)));row.add("outputs",outputs);values.add(row);}root.add("recipes",values);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
    private PaRecipes(){}
}
