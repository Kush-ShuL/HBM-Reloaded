package com.hbm.machine.press;

import com.hbm.item.StampItem.StampType;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;

import com.hbm.registry.HbmItemTags;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** PressRecipes.registerDefaults's original Mats.orderedList WIRE loop, installed into the native recipe manager. */
public final class PressWireRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<PressRecipe>>> INSTALLED=new WeakHashMap<>();
    private static boolean registered;
    private PressWireRecipes() {}
    public static void register() {
        if(registered) return; registered=true;
        NeoForge.EVENT_BUS.addListener(PressWireRecipes::serverStarting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,PressWireRecipes::datapackSync);
    }
    private static void serverStarting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager()); }
    private static void datapackSync(OnDatapackSyncEvent event) {
        RecipeManager manager=event.getPlayerList().getServer().getRecipeManager();
        // NeoForge posts this before constructing ClientboundUpdateRecipesPacket for login and /reload.
        // Rebuild once per resource reload; ordinary additional logins reuse the authoritative native holders.
        if(event.getPlayer()==null || !INSTALLED.containsKey(manager)) install(manager);
    }
    private static void install(RecipeManager manager) {
        if (PressRecipeConfig.hasReplacement()) return; // Source replacement skips registerDefaults,including WIRE.
        var previous=INSTALLED.getOrDefault(manager,Map.of());
        Map<ResourceLocation,RecipeHolder<?>> merged=new LinkedHashMap<>();
        for(RecipeHolder<?> holder:manager.getOrderedRecipes()) if(previous.get(holder.id())!=holder) merged.put(holder.id(),holder);
        Map<ResourceLocation,RecipeHolder<PressRecipe>> generated=new LinkedHashMap<>();
        MaterialShape wire=HbmMaterials.shape("WIRE"),ingot=HbmMaterials.shape("INGOT");
        Set<String> registeredTags=Set.copyOf(HbmItemTags.allTags());
        for(NtmMaterial material:HbmMaterials.ordered()) {
            // Preserve the exact first material alias used by MaterialShapes.INGOT.make(mat),
            // the actual native tag-existence condition, and original material order.
            String inputTag=HbmItemTags.material(ingot.symbol(),material.names().getFirst());
            if(!material.autogen().contains(wire) || !registeredTags.contains(inputTag)) continue;
            ResourceLocation id=ResourceLocation.fromNamespaceAndPath("hbm","press/generated_wire/"+material.id());
            // Native datapacks may explicitly replace a generated recipe by its stable material-ID path.
            if(merged.containsKey(id)) continue;
            var ingredient=net.minecraft.world.item.crafting.Ingredient.of(HbmItemTags.key(inputTag));
            var recipe=new RecipeHolder<>(id,new PressRecipe(ingredient,StampType.WIRE,material.make(AutogenContent.item("wire_fine"),8)));
            generated.put(id,recipe); merged.put(id,recipe);
        }
        if(!generated.isEmpty() || !previous.isEmpty()) manager.replaceRecipes(merged.values());
        INSTALLED.put(manager,generated);
    }
}
