package com.hbm.machine.arcwelder;

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

/** Original hbmArcWelder.json replacement/export grammar, ordered defaults and native recipe sync. */
public final class ArcWelderRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    public static void register(){
        try(var stream=ArcWelderRecipes.class.getResourceAsStream("/hbm/arc-welder-item-aliases.json")){if(stream==null)throw new IllegalStateException("Missing original arcwelder aliases");JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<ArcWelderRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(ArcWelderContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static ArcWelderRecipe read(JsonObject row,HolderLookup.Provider lookup,int order){
        var inputs=new ArrayList<GenericIngredient>();for(var value:row.getAsJsonArray("inputs"))inputs.add(GenericRecipeConfigCodec.readIngredient(value.getAsJsonArray(),ALIASES));
        var fluids=new ArrayList<FluidStack>();if(row.has("fluid")){var spec=row.getAsJsonArray("fluid");var type=HbmFluids.fromName(spec.get(0).getAsString());if(type==HbmFluids.NONE&&!spec.get(0).getAsString().equals("NONE"))throw new IllegalArgumentException("Unregistered source fluid "+spec);fluids.add(new FluidStack(type,spec.get(1).getAsInt(),spec.size()>2?spec.get(2).getAsInt():0));}
        var output=GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),ALIASES);var outputs=List.of(new GenericOutput(List.of(new GenericOutput.Chance(output,1,0)),false));
        return new ArcWelderRecipe(new GenericRecipeData("arcwelder."+order,row.get("duration").getAsInt(),row.get("consumption").getAsLong(),inputs,fluids,outputs,List.of(),List.of(),"",output.copy(),false,"",order));
    }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmArcWelder.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==ArcWelderContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{try(var stream=ArcWelderRecipes.class.getResourceAsStream("/hbm/arc-welder-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing original arcwelder defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().deepCopy();}Files.writeString(directory.resolve("_hbmArcWelder.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var recipe=read(value.getAsJsonObject(),server.registryAccess(),order);var id=ArcWelderContent.id("arcwelder/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,recipe));}catch(Exception error){UNRESOLVED.add("row"+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original arcwelder recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("{} actual ArcWelder recipes await true dependencies: {}",UNRESOLVED.size(),UNRESOLVED);
    }
    private ArcWelderRecipes(){}
}
