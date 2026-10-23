package com.hbm.client.airintake;
import com.hbm.machine.airintake.AirIntakeContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
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
public final class AirIntakeJeiPlugin implements IModPlugin {
    private static final RecipeType<AirIntakeViews.View> TYPE=RecipeType.create("hbm","air_intake",AirIntakeViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:air_intake_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,List.of(AirIntakeViews.view()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,AirIntakeContent.ITEM.get());}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,List.of(new ItemStack(AirIntakeContent.ITEM.get())));}
    private static final class Category implements IRecipeCategory<AirIntakeViews.View> {
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(AirIntakeContent.ITEM.get()));}
        @Override public RecipeType<AirIntakeViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_intake");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,AirIntakeViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.CATALYST,48,24).addItemStack(new ItemStack(AirIntakeContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}
        @Override public void draw(AirIntakeViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){AirIntakeViews.draw(graphics,0,0);}
    }
}
