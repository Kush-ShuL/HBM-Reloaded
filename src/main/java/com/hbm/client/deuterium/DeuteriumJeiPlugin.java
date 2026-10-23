package com.hbm.client.deuterium;
import com.hbm.machine.deuterium.DeuteriumContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class DeuteriumJeiPlugin implements IModPlugin {
    private static final RecipeType<DeuteriumViews.Recipe> TYPE=RecipeType.create("hbm","deuterium",DeuteriumViews.Recipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:deuterium_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,List.of(DeuteriumViews.Recipe.WATER_TO_HEAVYWATER));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,DeuteriumContent.EXTRACTOR_ITEM.get(),DeuteriumContent.TOWER_ITEM.get());}
    private static final class Category implements IRecipeCategory<DeuteriumViews.Recipe>{
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(DeuteriumContent.EXTRACTOR_ITEM.get()));}
        @Override public RecipeType<DeuteriumViews.Recipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return DeuteriumContent.EXTRACTOR.get().getName();}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,DeuteriumViews.Recipe recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(DeuteriumViews.input());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(DeuteriumViews.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(List.of(new ItemStack(DeuteriumContent.EXTRACTOR_ITEM.get()),new ItemStack(DeuteriumContent.TOWER_ITEM.get())));}
        @Override public void draw(DeuteriumViews.Recipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){DeuteriumViews.draw(graphics,0,0);}
    }
}
