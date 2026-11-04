package com.hbm.machine.press;

import com.hbm.item.StampItem.StampType;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import com.hbm.machine.shredder.ShredderConfig;

import com.hbm.registry.HbmItemTags;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Material plate and wire recipes are rebuilt from the current datapack's real item tags. */
public final class PressMaterialRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<PressRecipe>>> INSTALLED=new WeakHashMap<>();
    private static boolean registered;
    private PressMaterialRecipes() {}
    public static void register() {
        if(registered) return; registered=true;
        NeoForge.EVENT_BUS.addListener(PressMaterialRecipes::serverStarting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,PressMaterialRecipes::datapackSync);
    }
    private static void serverStarting(ServerAboutToStartEvent event) { install(event.getServer().getRecipeManager()); }
    private static void datapackSync(OnDatapackSyncEvent event) {
        RecipeManager manager=event.getPlayerList().getServer().getRecipeManager();
        // NeoForge posts this before constructing ClientboundUpdateRecipesPacket for login and /reload.
        // Rebuild once per resource reload; ordinary additional logins reuse the authoritative native holders.
        if(event.getPlayer()==null || !INSTALLED.containsKey(manager)) install(manager);
    }
    private static void install(RecipeManager manager) {
        if (PressRecipeConfig.hasReplacement()) return;
        var previous=INSTALLED.getOrDefault(manager,Map.of());
        Map<ResourceLocation,RecipeHolder<?>> merged=new LinkedHashMap<>();
        for(RecipeHolder<?> holder:manager.getOrderedRecipes()) if(previous.get(holder.id())!=holder) merged.put(holder.id(),holder);
        Map<ResourceLocation,RecipeHolder<PressRecipe>> generated=new LinkedHashMap<>();
        MaterialShape wire=HbmMaterials.shape("WIRE"),ingot=HbmMaterials.shape("INGOT"),plate=HbmMaterials.shape("PLATE");
        for(NtmMaterial material:HbmMaterials.ordered()) {
            String inputTag=HbmItemTags.material(ingot.symbol(),material.names().getFirst());
            if(!HbmItemTags.hasItems(inputTag)) continue;
            var ingredient=net.minecraft.world.item.crafting.Ingredient.of(HbmItemTags.key(inputTag));
            var plateOutput=HbmItemTags.preferredItem(HbmItemTags.material(plate.symbol(),material.names().getFirst()),ShredderConfig.preferredOutputMods());
            if(!plateOutput.isEmpty()) add(merged,generated,"plate",material,new PressRecipe(ingredient,StampType.PLATE,plateOutput));
            if(material.autogen().contains(wire)) add(merged,generated,"wire",material,new PressRecipe(ingredient,StampType.WIRE,material.make(AutogenContent.item("wire_fine"),8)));
        }
        if(!generated.isEmpty() || !previous.isEmpty()) manager.replaceRecipes(merged.values());
        INSTALLED.put(manager,generated);
    }
    private static void add(Map<ResourceLocation,RecipeHolder<?>> merged,Map<ResourceLocation,RecipeHolder<PressRecipe>> generated,String shape,NtmMaterial material,PressRecipe recipe) {
        ResourceLocation id=ResourceLocation.fromNamespaceAndPath("hbm","press/generated_"+shape+"/"+material.id());
        if(merged.containsKey(id)) return;
        var holder=new RecipeHolder<>(id,recipe);
        generated.put(id,holder); merged.put(id,holder);
    }
}
