package com.hbm.machine.soldering;

import com.google.gson.*;
import com.hbm.config.GeneralModes;
import com.hbm.fluid.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original hbmSoldering.json replacement/export grammar, ordered defaults and native recipe sync. */
public final class SolderingRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    public static void register(){
        try(var stream=SolderingRecipes.class.getResourceAsStream("/hbm/soldering-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing original soldering aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<SolderingRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(SolderingContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static List<GenericIngredient> lane(JsonObject row,String key){var result=new ArrayList<GenericIngredient>();for(var value:row.getAsJsonArray(key))result.add(GenericRecipeConfigCodec.readIngredient(value.getAsJsonArray(),ALIASES));return result;}
    private static SolderingRecipe read(JsonObject row,HolderLookup.Provider lookup,int order){
        var topping=lane(row,"toppings");var pcb=lane(row,"pcb");var solder=lane(row,"solder");var inputs=new ArrayList<>(topping);inputs.addAll(pcb);inputs.addAll(solder);
        var fluids=new ArrayList<FluidStack>();if(row.has("fluid")){var spec=row.getAsJsonArray("fluid");var type=HbmFluids.fromName(spec.get(0).getAsString());if(type==HbmFluids.NONE&&!spec.get(0).getAsString().equals("NONE"))throw new IllegalArgumentException("Unregistered source fluid "+spec);fluids.add(new FluidStack(type,spec.get(1).getAsInt(),spec.size()>2?spec.get(2).getAsInt():0));}
        var output=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),ALIASES);var outputs=List.of(new GenericOutput(List.of(new GenericOutput.Chance(output,1,0)),false));
        var data=new GenericRecipeData("soldering."+order,row.get("duration").getAsInt(),row.get("consumption").getAsLong(),inputs,fluids,outputs,List.of(),List.of(),"",output.copy(),false,"",order);
        return new SolderingRecipe(data,topping.size(),pcb.size());
    }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmSoldering.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==SolderingContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{String mode=GeneralModes.mode528()?"528":GeneralModes.lbsm()&&GeneralModes.flag("LBSM_recipeSimpleCrafting")?"lbsm":"normal";try(var stream=SolderingRecipes.class.getResourceAsStream("/hbm/soldering-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing original soldering defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject(mode).deepCopy();}Files.writeString(directory.resolve("_hbmSoldering.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var recipe=read(value.getAsJsonObject(),server.registryAccess(),order);var id=SolderingContent.id("soldering/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("row"+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original soldering recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);for(var unresolved:UNRESOLVED)LogUtils.getLogger().warn("Unbound soldering recipe: {}",unresolved);
    }
    private SolderingRecipes(){}
}
