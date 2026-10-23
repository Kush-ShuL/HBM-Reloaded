package com.hbm.client.oreslopper;
import com.hbm.machine.oreslopper.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class OreSlopperJeiPlugin implements IModPlugin {
    static final RecipeType<OreSlopperViews.Display> TYPE=RecipeType.create("hbm","oreslopper",OreSlopperViews.Display.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","oreslopper_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,java.util.List.of(new OreSlopperViews.Display()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,OreSlopperContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(OreSlopperScreen.class,62,18,34,34,TYPE);}
    private static final class Category implements IRecipeCategory<OreSlopperViews.Display>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(OreSlopperContent.ITEM.get()));}@Override public RecipeType<OreSlopperViews.Display> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("jei.hbm.oreslopper");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,OreSlopperViews.Display recipe,IFocusGroup focus){var input=OreSlopperViews.inputs();for(int i=0;i<input.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,OreSlopperViews.INPUT[i][0],OreSlopperViews.INPUT[i][1]).addItemStack(input.get(i));builder.addSlot(RecipeIngredientRole.RENDER_ONLY,75,31).addItemStack(new ItemStack(OreSlopperContent.ITEM.get()));var out=OreSlopperViews.outputs();for(int i=0;i<out.size();i++)if(!out.get(i).isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,OreSlopperViews.OUTPUT[i][0],OreSlopperViews.OUTPUT[i][1]).addItemStack(out.get(i));}
        @Override public void draw(OreSlopperViews.Display recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){OreSlopperViews.draw(graphics,0,0);}
    }
}
