package com.hbm.client.annihilator;
import com.hbm.machine.annihilator.AnnihilatorContent;
import java.util.Locale;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class AnnihilatorJeiPlugin implements IModPlugin {
 static final RecipeType<AnnihilatorViews.View> TYPE=RecipeType.create("hbm","annihilating",AnnihilatorViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:annihilating_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){visible=AnnihilatorViews.recipes();event.addRecipes(TYPE,visible);}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,AnnihilatorContent.ITEM.get());}
 private static mezz.jei.api.runtime.IJeiRuntime runtime;
 private static java.util.List<AnnihilatorViews.View> visible=java.util.List.of();
 @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime value){runtime=value;AnnihilatorRecipeSyncClient.registerViewer("jei",AnnihilatorJeiPlugin::refresh);refresh();}
 @Override public void onRuntimeUnavailable(){AnnihilatorRecipeSyncClient.removeViewer("jei");runtime=null;visible=java.util.List.of();}
 private static void refresh(){if(runtime==null)return;var manager=runtime.getRecipeManager();manager.hideRecipes(TYPE,visible);visible=AnnihilatorViews.recipes();manager.addRecipes(TYPE,visible);}
 private static final class Category implements IRecipeCategory<AnnihilatorViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(AnnihilatorContent.ITEM.get()));}@Override public RecipeType<AnnihilatorViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.annihilator");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,AnnihilatorViews.View recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(recipe.inputs()).addRichTooltipCallback((view,tooltip)->tooltip.add(Component.translatable("gui.hbm.annihilator.threshold",String.format(Locale.US,"%,d",recipe.amount()))));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}}
}
