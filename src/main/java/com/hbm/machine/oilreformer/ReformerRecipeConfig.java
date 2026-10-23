package com.hbm.machine.oilreformer;

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

/** Source hbmReforming.json replacement grammar; synchronized native processing recipes, never viewer-only. */
public final class ReformerRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private ReformerRecipeConfig(){}
    public static void register(){
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<ReformerRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(ReformerContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(r->r.data().order())).toList();}
    public static ReformerRecipe find(RecipeManager manager,HbmFluidType type){return recipes(manager).stream().filter(r->r.data().inputFluids().getFirst().type==type).findFirst().orElse(null);}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmReforming.json");
        try{
            Files.createDirectories(directory);JsonObject root;if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==ReformerContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{try(var stream=ReformerRecipeConfig.class.getResourceAsStream("/hbm/oil-reformer-defaults.json")){if(stream==null)throw new IllegalStateException("Missing source oil_reforming recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmReforming.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=fluid(row.get("input").getAsString());var outputs=new ArrayList<FluidStack>();for(int i=0;i<3;i++){var array=row.getAsJsonArray("output"+(i+1));outputs.add(new FluidStack(fluid(array.get(0).getAsString()),array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0));}
                var data=new GenericRecipeData("oil_reforming."+input.getName(),1,20000,List.of(),List.of(new FluidStack(input,100)),List.of(),outputs,List.of(),"",ItemStack.EMPTY,false,"",order);
                var id=ResourceLocation.fromNamespaceAndPath("hbm","oil_reforming/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.values().removeIf(holder->holder.value() instanceof ReformerRecipe old&&old.data().inputFluids().getFirst().type==input);additions.put(id,new RecipeHolder<>(id,new ReformerRecipe(data)));
            }catch(Exception error){UNRESOLVED.add("row "+order+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original oil_reforming recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);if(!UNRESOLVED.isEmpty())LogUtils.getLogger().warn("Original oil_reforming source recipes awaiting items: {}",UNRESOLVED);
    }
    private static HbmFluidType fluid(String name){var type=HbmFluids.fromName(name);if(type==HbmFluids.NONE&&!name.equals("NONE"))throw new IllegalArgumentException("Unknown oil_reforming fluid "+name);return type;}
}
