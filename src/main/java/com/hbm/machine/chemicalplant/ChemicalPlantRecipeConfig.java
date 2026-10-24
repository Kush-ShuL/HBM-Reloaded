package com.hbm.machine.chemicalplant;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original hbmChemicalPlant.json replacement grammar, source mode defaults, and recipe synchronization. */
public final class ChemicalPlantRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private static boolean mode528,lessBullshitMode,simpleChemistry=true,pressurizedRecipes=true;
    private ChemicalPlantRecipeConfig(){}
    public static void register(){
        try(var stream=ChemicalPlantRecipeConfig.class.getResourceAsStream("/hbm/chemical-plant-item-aliases.json")){
            if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    static void setup(FMLCommonSetupEvent event){event.enqueueWork(ChemicalPlantRecipeConfig::loadMode);}
    private static void loadMode(){mode528=com.hbm.config.GeneralModes.mode528();lessBullshitMode=com.hbm.config.GeneralModes.lbsm();simpleChemistry=com.hbm.config.GeneralModes.flag("LBSM_recipeSimpleChemistry");pressurizedRecipes=com.hbm.config.GeneralModes.pressurizedRecipes();}
    private static boolean bool(JsonObject root,String key,boolean fallback){return root.has(key)?root.get(key).getAsBoolean():fallback;}
    public static List<ChemicalPlantRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(ChemicalPlantContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){loadMode();
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmChemicalPlant.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){
                merged.values().removeIf(holder->holder.value().getType()==ChemicalPlantContent.TYPE.get());
                try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}
            }else{
                // GeneralConfig forces LBSM off in528 and pressure off outside528.
                String mode=mode528&&pressurizedRecipes?"pressurized":!mode528&&lessBullshitMode&&simpleChemistry?"lbsm":"normal";
                try(var stream=ChemicalPlantRecipeConfig.class.getResourceAsStream("/hbm/chemical-plant-recipes.json")){
                    if(stream==null)throw new IllegalStateException("Missing original chemical plant source recipes");
                    root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject(mode).deepCopy();
                }
                root.addProperty("comment","改名为 hbmChemicalPlant.json 后替换完整默认配方表。输入按位置匹配，压力和流体池名称须准确填写。");
                Files.writeString(directory.resolve("_hbmChemicalPlant.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
            }
            var names=new HashSet<String>();int order=0;
            for(var value:root.getAsJsonArray("recipes")){
                String label="row "+order;
                try{
                    var row=value.getAsJsonObject();label=row.has("name")?row.get("name").getAsString():label;
                    var d=GenericRecipeConfigCodec.read(row,server.registryAccess(),ALIASES);
                    if(!names.add(d.name()))throw new IllegalArgumentException("Duplicate source recipe "+d.name());
                    d=new GenericRecipeData(d.name(),d.duration(),d.power(),d.inputItems(),d.inputFluids(),d.outputItems(),d.outputFluids(),d.pools(),d.group(),d.icon(),d.named(),d.nameWrapper(),order);
                    var id=ResourceLocation.fromNamespaceAndPath("hbm","chemical/"+(Files.isRegularFile(active)?"config/":"default/")+order);
                    additions.put(id,new RecipeHolder<>(id,new ChemicalPlantRecipe(d)));
                }catch(Exception error){UNRESOLVED.add(label+": "+error.getMessage());}
                order++;
            }
        }catch(Exception error){LogUtils.getLogger().error("Cannot load original chemical plant recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);
        Blueprints.registerRecipes(ResourceLocation.fromNamespaceAndPath("hbm","chemical"),recipes(manager));
        for(var error:UNRESOLVED)LogUtils.getLogger().warn("Unbound chemical plant recipe: {}",error);
    }
}
