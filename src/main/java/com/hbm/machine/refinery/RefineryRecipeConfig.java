package com.hbm.machine.refinery;

import com.google.gson.*;
import com.hbm.fluid.*;
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

/** Source hbmRefinery.json replacement grammar; synchronized native processing recipes, never viewer-only. */
public final class RefineryRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new HashMap<>();private static final List<String> UNRESOLVED=new ArrayList<>();
    private RefineryRecipeConfig(){}
    public static void register(){
        try(var stream=RefineryRecipeConfig.class.getResourceAsStream("/hbm/chemical-plant-item-aliases.json")){if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(e->ALIASES.put(e.getKey(),e.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<RefineryRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(RefineryContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    public static RefineryRecipe find(RecipeManager manager,HbmFluidType type){return recipes(manager).stream().filter(r->r.data().inputFluids().getFirst().type==type).findFirst().orElse(null);}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmRefinery.json");
        try{
            Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==RefineryContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{try(var stream=RefineryRecipeConfig.class.getResourceAsStream("/hbm/large-machine-refinery-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source refinery recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmRefinery.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=fluid(row.get("input").getAsString());var outputs=new ArrayList<FluidStack>();for(int i=0;i<4;i++){var array=row.getAsJsonArray("output"+i);outputs.add(new FluidStack(fluid(array.get(0).getAsString()),Math.multiplyExact(array.get(1).getAsInt(),10),array.size()>2?array.get(2).getAsInt():0));}
                ItemStack solid=row.has("solid")&&!row.get("solid").isJsonNull()?GenericRecipeConfigCodec.readStack(row.getAsJsonArray("solid"),ALIASES):ItemStack.EMPTY;
                var data=new GenericRecipeData("refinery."+input.getName(),10,5,List.of(),List.of(new FluidStack(input,1000)),solid.isEmpty()?List.of():List.of(new GenericOutput(solid)),outputs,List.of(),"",ItemStack.EMPTY,false,"",order);
                var id=ResourceLocation.fromNamespaceAndPath("hbm","refinery/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,new RefineryRecipe(data)));
            }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original refinery recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Original refinery source recipes awaiting items: {}",UNRESOLVED);
    }
    private static HbmFluidType fluid(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown refinery fluid "+name);return type;}
}
