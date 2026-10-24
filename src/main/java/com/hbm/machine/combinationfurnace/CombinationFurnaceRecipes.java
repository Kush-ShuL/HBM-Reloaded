package com.hbm.machine.combinationfurnace;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.machine.electricfurnace.ElectricFurnaceBlockEntity;
import com.hbm.ore.RawOreContent;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.recipe.generic.*;
import com.hbm.registry.HbmOreDictionary;
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
/** Real53 defaults/hbmCombination.json replacement, exact Item+damage before source ore-name order. */
public final class CombinationFurnaceRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
    public static void register(){
        try(var stream=CombinationFurnaceRecipes.class.getResourceAsStream("/hbm/combination-furnace184-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing actual combination-furnace aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(value->ALIASES.put(value.getKey(),value.getValue().getAsString()));}catch(Exception error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<CombinationFurnaceRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(CombinationFurnaceContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static CombinationFurnaceRecipe find(RecipeManager manager,ItemStack stack){
        if(stack.isEmpty())return null;var canonical=RawOreContent.processingStack(stack);var values=recipes(manager);
        for(int i=values.size()-1;i>=0;i--){var recipe=values.get(i);if(!recipe.dictionary()&&recipe.matchesItem(canonical))return recipe;}
        for(String raw:HbmOreDictionary.names(canonical))for(int i=values.size()-1;i>=0;i--){var recipe=values.get(i);if(recipe.input().ingredient().getCustomIngredient() instanceof LegacyOreIngredient ore&&ore.tag().toString().equals("hbm:legacy_ore_dict/"+raw.toLowerCase(Locale.ROOT)))return recipe;}
        return null;
    }
    private static boolean sameKey(CombinationFurnaceRecipe first,CombinationFurnaceRecipe second){
        var a=first.input().ingredient().getCustomIngredient();var b=second.input().ingredient().getCustomIngredient();
        if(a instanceof LegacyOreIngredient left)return b instanceof LegacyOreIngredient right&&left.tag().equals(right.tag());
        if(b instanceof LegacyOreIngredient)return false;var left=first.input().ingredient().getItems();var right=second.input().ingredient().getItems();return left.length==1&&right.length==1&&ElectricFurnaceBlockEntity.sameLegacyItem(left[0],right[0]);
    }
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var recipe:manager.getOrderedRecipes())if(previous.get(recipe.id())!=recipe)merged.put(recipe.id(),recipe);
        var added=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path dir=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),file=dir.resolve("hbmCombination.json");boolean custom=Files.isRegularFile(file);
        try{
            Files.createDirectories(dir);JsonObject root;
            if(custom){merged.values().removeIf(holder->holder.value().getType()==CombinationFurnaceContent.TYPE.get());try(var reader=Files.newBufferedReader(file)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else try(var stream=CombinationFurnaceRecipes.class.getResourceAsStream("/hbm/combination-furnace184-defaults.json")){if(stream==null)throw new IllegalStateException("Missing actual53 combination defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}
            int order=0;for(var value:root.getAsJsonArray("recipes")){
                try{var recipe=read(value.getAsJsonObject(),order);added.values().removeIf(old->sameKey((CombinationFurnaceRecipe)old.value(),recipe));var id=ResourceLocation.parse("hbm:combination/"+(custom?"config/":"default/")+order);added.put(id,new RecipeHolder<>(id,recipe));}
                catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;
            }
            if(!custom)template(dir.resolve("_hbmCombination.json"),added.values().stream().map(value->(CombinationFurnaceRecipe)value.value()).toList());
        }catch(Exception error){LogUtils.getLogger().error("Cannot load actual CombinationRecipes replacement",error);}
        added.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,added);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().error("Actual combination furnace recipes unresolved: {}",UNRESOLVED);
    }
    private static CombinationFurnaceRecipe read(JsonObject row,int order){
        JsonArray spec=row.getAsJsonArray("input").deepCopy();
        // getOutput constructs a plain ComparableStack; it never calls NBTStack.matchesRecipe.
        if(spec.get(0).getAsString().equals("nbt"))spec.set(0,new JsonPrimitive("item"));
        var parsed=GenericRecipeConfigCodec.readIngredient(spec,ALIASES);var input=new GenericIngredient(parsed.ingredient(),1);
        ItemStack output=row.has("output")?GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),ALIASES):ItemStack.EMPTY;
        var fluids=new ArrayList<FluidStack>();if(row.has("fluid")){var fluid=row.getAsJsonArray("fluid");String name=fluid.get(0).getAsString();var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unregistered original fluid "+name);fluids.add(new FluidStack(type,fluid.get(1).getAsInt(),fluid.size()>2?fluid.get(2).getAsInt():0));}
        return new CombinationFurnaceRecipe(new GenericRecipeData("combination.source."+order,0,0,List.of(input),List.of(),output.isEmpty()?List.of():List.of(new GenericOutput(output)),fluids,List.of(),"",ItemStack.EMPTY,false,"",order));
    }
    private static void template(Path file,List<CombinationFurnaceRecipe> values)throws Exception{var root=new JsonObject();var rows=new JsonArray();for(var recipe:values){var row=new JsonObject();row.add("input",GenericRecipeConfigCodec.write(recipe.data()).getAsJsonArray("inputItem").get(0));if(!recipe.solid().isEmpty())row.add("output",GenericRecipeConfigCodec.writeStack(recipe.solid()));if(recipe.fluid()!=null){var fluid=recipe.fluid();var spec=new JsonArray();spec.add(fluid.type.getName());spec.add(fluid.fill);if(fluid.pressure!=0)spec.add(fluid.pressure);row.add("fluid",spec);}rows.add(row);}root.add("recipes",rows);Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
    private CombinationFurnaceRecipes(){}
}
