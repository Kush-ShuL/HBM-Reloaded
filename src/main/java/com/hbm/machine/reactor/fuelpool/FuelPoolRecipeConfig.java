package com.hbm.machine.reactor.fuelpool;

import com.google.gson.*;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
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

/** Original hbmFuelpool.json replaces the entire source map. Native recipe sync keeps viewer/server identical. */
public final class FuelPoolRecipeConfig {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    private static final List<String> UNBOUND=new ArrayList<>();
    public static void register(){NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{var server=event.getPlayerList().getServer();if(event.getPlayer()==null||!INSTALLED.containsKey(server.getRecipeManager()))install(server);});}
    public static List<String> unbound(){return List.copyOf(UNBOUND);}
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server){var manager=server.getRecipeManager();var previous=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();for(var holder:manager.getOrderedRecipes())if(previous.get(holder.id())!=holder)merged.put(holder.id(),holder);var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();UNBOUND.clear();var directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes");var active=directory.resolve("hbmFuelpool.json");try{Files.createDirectories(directory);JsonObject root;boolean configured=Files.isRegularFile(active);if(configured){merged.values().removeIf(holder->holder.value().getType()==FuelPoolContent.TYPE.get());try(var reader=Files.newBufferedReader(active)){root=JsonParser.parseReader(reader).getAsJsonObject();}}else{try(var stream=FuelPoolRecipeConfig.class.getResourceAsStream("/hbm/fuel-pool-source-recipes.json")){if(stream==null)throw new IllegalStateException("Missing source fuel pool recipes");root=JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();}Files.writeString(directory.resolve("_hbmFuelpool.json"),new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");}var aliases=new HashMap<String,String>();try(var stream=FuelPoolRecipeConfig.class.getResourceAsStream("/hbm/fuel-pool-item-aliases.json")){if(stream!=null)JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->aliases.put(entry.getKey(),entry.getValue().getAsString()));}int order=0;var bySourceKey=new LinkedHashMap<String,RecipeHolder<?>>();for(var value:root.getAsJsonArray("recipes")){try{var row=value.getAsJsonObject();var input=row.getAsJsonArray("input");if(!input.get(0).getAsString().equals("item"))throw new IllegalArgumentException("FuelPoolRecipes.readRecipe accepts only ComparableStack inputs");var recipe=new FuelPoolRecipe(GenericRecipeConfigCodec.readIngredient(input,aliases).ingredient(),GenericRecipeConfigCodec.readStack(row.getAsJsonArray("output"),aliases),order);var id=FuelPoolContent.id("fuel_pool/"+(configured?"config/":"default/")+order);var sourceStack=recipe.input().getItems()[0];String key=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(sourceStack.getItem())+"@"+sourceStack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),sourceStack.getDamageValue());bySourceKey.put(key,new RecipeHolder<>(id,recipe));}catch(Exception error){UNBOUND.add("row "+order+": "+error.getMessage());}order++;}for(var holder:bySourceKey.values())additions.put(holder.id(),holder);}catch(Exception error){LogUtils.getLogger().error("Cannot load source fuel-pool recipe config",error);}additions.forEach(merged::put);manager.replaceRecipes(merged.values());INSTALLED.put(manager,additions);for(var error:UNBOUND)LogUtils.getLogger().warn("Unbound source fuel-pool recipe: {}",error);}
    private FuelPoolRecipeConfig(){}
}
