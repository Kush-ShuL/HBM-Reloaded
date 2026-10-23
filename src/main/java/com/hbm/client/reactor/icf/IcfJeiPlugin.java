package com.hbm.client.reactor.icf;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.icf.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class IcfJeiPlugin implements IModPlugin{
 private static final RecipeType<IcfTooling.View> TYPE=RecipeType.create("hbm","icf_tooling",IcfTooling.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:icf_core_jei");}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,IcfReactorContent.materialStacks());}@Override public void registerItemSubtypes(ISubtypeRegistration event){for(var item:java.util.List.of(IcfReactorContent.COMPONENT_ITEM.get(),IcfReactorContent.LASER_COMPONENT_ITEM.get()))event.registerSubtypeInterpreter(item,new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,IcfTooling.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){for(var recipe:IcfTooling.recipes())for(var tool:recipe.tools())event.addRecipeCatalyst(tool,TYPE);}
 private static final class Category implements IRecipeCategory<IcfTooling.View>{private final IDrawable background,icon;Category(IGuiHelper gui){background=gui.createBlankDrawable(166,65);icon=gui.createDrawableItemStack(IcfTooling.recipes().getFirst().output());}@Override public RecipeType<IcfTooling.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("recipe.hbm.icf.tooling");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder layout,IcfTooling.View recipe,IFocusGroup focus){var inputs=recipe.inputs();for(int i=0;i<inputs.size();i++)layout.addSlot(RecipeIngredientRole.INPUT,48-i*18,24).addItemStacks(inputs.get(i));layout.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(recipe.tools());layout.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}@Override public void draw(IcfTooling.View recipe,IRecipeSlotsView slots,GuiGraphics g,double x,double y){IcfTooling.draw(recipe,g,0,0);}}
}
