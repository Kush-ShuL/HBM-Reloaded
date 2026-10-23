package com.hbm.config;

import com.hbm.registry.HbmOreDictionary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** The finite output replacement in the original CraftingManager.crumple(). */
public final class SourceMekanismCrafting {
    private static final Set<RecipeManager> INSTALLED = Collections.newSetFromMap(new WeakHashMap<>());
    private static boolean registered;
    private SourceMekanismCrafting() { }

    public static void register() {
        if (registered) return;
        registered = true;
        if (ModList.get().isLoaded("mekanism")) {
            // The fourth original name, alloyUltimate, is already registered by GeneralModes.
            for (String name : new String[] {"battery", "alloyAdvanced", "ingotRefinedObsidian"}) HbmOreDictionary.registerName(name);
        }
        NeoForge.EVENT_BUS.addListener(SourceMekanismCrafting::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, SourceMekanismCrafting::sync);
    }

    private static void starting(ServerAboutToStartEvent event) { install(event.getServer()); }
    private static void sync(OnDatapackSyncEvent event) {
        var server = event.getPlayerList().getServer();
        if (event.getPlayer() == null || !INSTALLED.contains(server.getRecipeManager())) install(server);
    }
    private static void install(MinecraftServer server) {
        var manager = server.getRecipeManager();
        if (!GeneralModes.flag("mekanismRecipeChangesEnabled")) { INSTALLED.add(manager); return; }
        boolean mekanism = ModList.get().isLoaded("mekanism");
        boolean generators = ModList.get().isLoaded("mekanismgenerators");
        if (!mekanism && !generators) { INSTALLED.add(manager); return; }
        var retained = new ArrayList<RecipeHolder<?>>();
        boolean changed = false;
        for (var holder : manager.getOrderedRecipes()) {
            boolean remove = false;
            // Original IRecipe list is the workbench list, not every industrial RecipeType.
            if (holder.value() instanceof CraftingRecipe) {
                var result = holder.value().getResultItem(server.registryAccess());
                if (!result.isEmpty()) {
                    var item = BuiltInRegistries.ITEM.getKey(result.getItem());
                    if (mekanism && item.equals(id("mekanism", "digital_miner"))) remove = true;
                    if (mekanism && item.equals(id("mekanism", "atomic_disassembler"))) remove = !holder.id().equals(id("hbm", "crafting/source292_atomic_disassembler"));
                    if (generators && item.equals(id("mekanismgenerators", "wind_generator"))) remove = !holder.id().equals(id("hbm", "crafting/source292_wind_generator"));
                }
            }
            if (remove) changed = true;
            else retained.add(holder);
        }
        if (changed) manager.replaceRecipes(retained);
        INSTALLED.add(manager);
    }
    private static ResourceLocation id(String namespace, String path) { return ResourceLocation.fromNamespaceAndPath(namespace, path); }
}
