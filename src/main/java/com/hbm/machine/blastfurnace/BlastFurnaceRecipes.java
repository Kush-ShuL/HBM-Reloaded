package com.hbm.machine.blastfurnace;

import com.google.gson.*;
import com.hbm.recipe.generic.*;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original ordered hbmBlastFurnace.json complete replacement, separate from legacy alloy/IMC. */
public final class BlastFurnaceRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    public static void register(){
        try(var stream=BlastFurnaceRecipes.class.getResourceAsStream("/hbm/blast-furnace-item-aliases.json")){if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));}catch(java.io.IOException error){throw new IllegalStateException(error);}
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});
    }
    public static List<BlastFurnaceRecipe> recipes(RecipeManager manager){return manager.getAllRecipesFor(BlastFurnaceContent.TYPE.get()).stream().map(RecipeHolder::value).sorted(Comparator.comparingInt(recipe->recipe.data().order())).toList();}
    public static List<String> unresolved(){return List.copyOf(UNRESOLVED);}
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNRESOLVED.clear();Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmBlastFurnace.json");
        try{
            Files.createDirectories(directory);JsonObject root;
            if(Files.isRegularFile(active)){merged.values().removeIf(holder->holder.value().getType()==BlastFurnaceContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}
            else{try(var stream=BlastFurnaceRecipes.class.getResourceAsStream("/hbm/blast-furnace-recipes.json")){if(stream==null)throw new IllegalStateException("Missing X5846 blast furnace defaults");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}root.addProperty("comment","改名为 hbmBlastFurnace.json 后替换完整默认配方表。物品输入不区分顺序；加工时长单位为游戏刻。");Files.writeString(directory.resolve("_hbmBlastFurnace.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}
            int order=0;var names=new HashSet<String>();for(var value:root.getAsJsonArray("recipes")){String label="row "+order;try{var row=value.getAsJsonObject();label=row.has("name")?row.get("name").getAsString():label;var d=GenericRecipeConfigCodec.read(row,server.registryAccess(),ALIASES);if(!names.add(d.name()))throw new IllegalArgumentException("Duplicate source name "+d.name());if(d.inputItems().isEmpty()||d.inputItems().size()>2||d.outputItems().size()>2||!d.inputFluids().isEmpty()||!d.outputFluids().isEmpty())throw new IllegalArgumentException("Source blast furnace recipe limits exceeded");d=new GenericRecipeData(d.name(),d.duration(),0,d.inputItems(),List.of(),d.outputItems(),List.of(),d.pools(),d.group(),d.icon(),d.named(),d.nameWrapper(),order);var id=ResourceLocation.fromNamespaceAndPath("hbm","blast_furnace/"+(Files.isRegularFile(active)?"config/":"default/")+order);additions.put(id,new RecipeHolder<>(id,new BlastFurnaceRecipe(d)));}catch(Exception error){UNRESOLVED.add(label+": "+error.getMessage());}order++;}
        }catch(Exception error){LogUtils.getLogger().error("Cannot load X5846 new blast furnace recipes",error);}
        additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);for(var error:UNRESOLVED)LogUtils.getLogger().warn("Unbound new blast furnace recipe: {}",error);
    }
    private BlastFurnaceRecipes(){}
}
