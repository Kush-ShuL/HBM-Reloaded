package com.hbm.item.bedrockprocessing;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.hbm.material.HbmMaterials;
import com.hbm.registry.HbmItemTags;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original OreDictManager fragment compaction: first real ore member in source prefix order. */
public final class BedrockFragmentRecipes {
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<?>>> INSTALLED=new WeakHashMap<>();
    public static void register(){
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event)->install(event.getServer()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,(OnDatapackSyncEvent event)->{if(event.getPlayer()==null||!INSTALLED.containsKey(event.getPlayerList().getServer().getRecipeManager()))install(event.getPlayerList().getServer());});
    }
    private static void install(MinecraftServer server){
        var manager=server.getRecipeManager();var old=INSTALLED.getOrDefault(manager,Map.of());var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes())if(old.get(holder.id())!=holder)merged.put(holder.id(),holder);
        var installed=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();var fragment=AutogenContent.item("bedrock_ore_fragment");var shape=HbmMaterials.shape("FRAGMENT");
        for(var material:HbmMaterials.ordered())if(material.autogen().contains(shape)){
            int alias=0;for(int materialAlias=0;materialAlias<material.names().size();materialAlias++){
                ItemStack output=ItemStack.EMPTY;boolean billet=false;
                for(String prefix:List.of("DUST","GEM","CRYSTAL","INGOT","BILLET","ORE")){
                    var first=HbmItemTags.items(HbmItemTags.material(prefix,material.names().getFirst())).findFirst();
                    if(first.isPresent()){output=first.get().copy();billet=prefix.equals("BILLET");break;}
                }
                if(output.isEmpty())output=new ItemStack(BuiltInRegistries.ITEM.get(BedrockProcessingContent.id("nothing")));
                if(output.isEmpty())throw new IllegalStateException("Original fragment fallback item is missing");
                var ingredient=new AlloyStackIngredient(material.make(fragment)).toVanilla();
                var pattern=ShapedRecipePattern.of(Map.of('#',ingredient),billet?List.of("###","###"):List.of("###","###","###"));
                var id=BedrockProcessingContent.id("bedrock_processing/fragment/"+material.symbol().toLowerCase(Locale.ROOT)+"_"+alias++);
                var holder=new RecipeHolder<>(id,new ShapedRecipe("",CraftingBookCategory.MISC,pattern,output));
                if(!merged.containsKey(id)){merged.put(id,holder);installed.put(id,holder);}
            }
        }
        manager.replaceRecipes(merged.values());INSTALLED.put(manager,installed);
    }
    private BedrockFragmentRecipes(){}
}
