package com.hbm.client.rtg;

import com.hbm.machine.rtg.RtgContent;
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
public final class RtgJeiPlugin implements IModPlugin {
    public static final RecipeType<RtgViews.Decay> TYPE=RecipeType.create("hbm","rtg",RtgViews.Decay.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rtg_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,RtgViews.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,RtgContent.ITEM.get());}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,RtgContent.creativeStacks("control"));}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(RtgContent.DEPLETED.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){
        @Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return RtgContent.DEPLETED.get().ordinal(stack);}
        @Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}
    });}
    private static final class Category implements IRecipeCategory<RtgViews.Decay>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(RtgContent.ITEM.get()));}
        @Override public RecipeType<RtgViews.Decay> getRecipeType(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.rtg");}
        @Override public IDrawable getBackground(){return background;}
        @Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,RtgViews.Decay recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.pellet());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.depleted());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(RtgContent.ITEM.get()));}
        @Override public void draw(RtgViews.Decay recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){RtgViews.draw(graphics,0,0);}
    }
}
