package com.hbm.client.recipe.generic;
import com.hbm.recipe.generic.*;
import java.util.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BlueprintClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{GenericRecipeData.setDetailKey(()->com.mojang.blaze3d.platform.InputConstants.isKeyDown(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(),org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT));var property=ResourceLocation.fromNamespaceAndPath("hbm","blueprint_kind");ItemProperties.register(Blueprints.ITEM.get(),property,(stack,level,entity,seed)->Blueprints.textureKind(stack));});}
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Recipes {@SubscribeEvent public static void updated(RecipesUpdatedEvent event){Blueprints.clearRecipes();Map<ResourceLocation,List<GenericMachineRecipe>> sets=new LinkedHashMap<>();for(var holder:event.getRecipeManager().getOrderedRecipes())if(holder.value() instanceof GenericMachineRecipe recipe){var type=BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());sets.computeIfAbsent(type,key->new ArrayList<>()).add(recipe);}sets.forEach((type,recipes)->Blueprints.registerRecipes(type,recipes.stream().sorted(Comparator.comparingInt(r->r.data().order())).toList()));}}
}
