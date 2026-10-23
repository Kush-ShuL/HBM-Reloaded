package com.hbm.machine.fractiontower;

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

/** Source hbmFractions.json replacement grammar; synchronized native processing recipes, never viewer-only. */
public final class FractionRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private static final Map<RecipeManager,Map<HbmFluidType,FractionRecipe>> INDEX=new WeakHashMap<>();
    private FractionRecipeConfig(){}
    public static void register(){
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<FractionRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(FractionContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    public static FractionRecipe find(RecipeManager manager,HbmFluidType type){return INDEX.computeIfAbsent(manager,key->{var index=new IdentityHashMap<HbmFluidType,FractionRecipe>();for(var recipe:recipes(key))index.putIfAbsent(recipe.data().inputFluids().getFirst().type,recipe);return index;}).get(type);}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();INDEX.remove(manager);var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmFractions.json");
        try{
            Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==FractionContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{try(var stream=FractionRecipeConfig.class.getResourceAsStream("/hbm/fraction-tower-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source fractiontower recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmFractions.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=fluid(row.get("input").getAsString());var outputs=new ArrayList<FluidStack>();for(int i=1;i<=2;i++){var array=row.getAsJsonArray("output"+i);outputs.add(new FluidStack(fluid(array.get(0).getAsString()),array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0));}
                var data=new GenericRecipeData("fractiontower."+input.getName(),10,0,List.of(),List.of(new FluidStack(input,100)),List.of(),outputs,List.of(),"",ItemStack.EMPTY,false,"",order);
                var id=ResourceLocation.fromNamespaceAndPath("hbm","fractiontower/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.values().removeIf(holder->holder.value() instanceof FractionRecipe old&&old.data().inputFluids().getFirst().type==input);additions.put(id,new RecipeHolder<>(id,new FractionRecipe(data)));
            }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original fractiontower recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Original fractiontower source recipes awaiting items: {}",UNRESOLVED);
    }
    private static HbmFluidType fluid(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown fractiontower fluid "+name);return type;}
}
