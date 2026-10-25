package com.hbm.client.precisionassembler;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.precisionassembler.*;
import com.hbm.recipe.generic.*;
import java.util.*;
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
public final class PrecisionAssemblerJeiPlugin implements IModPlugin {
    static final RecipeType<PrecisionAssemblerRecipe> TYPE=RecipeType.create("hbm","precision_assembly",PrecisionAssemblerRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","precision_assembly_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PrecisionAssemblerRecipeConfig.recipes(level.getRecipeManager()).stream().filter(PrecisionAssemblerViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,PrecisionAssemblerContent.ITEM.get());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PrecisionAssemblerContent.BROKEN.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return BrokenItem.sourceData(stack).toString();}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
    private static final class Category implements IRecipeCategory<PrecisionAssemblerRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(PrecisionAssemblerContent.ITEM.get()));}
        @Override public RecipeType<PrecisionAssemblerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machinePrecAss");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PrecisionAssemblerRecipe recipe,IFocusGroup focuses){var display=PrecisionAssemblerViews.drawing(recipe);var in=PrecisionAssemblerViews.inputs(recipe);var out=PrecisionAssemblerViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(PrecisionAssemblerViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(PrecisionAssemblerViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(PrecisionAssemblerViews.outputTooltip(recipe,lane,stack))));}int offset=PrecisionAssemblerViews.offset(recipe);builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,31).addItemStack(new ItemStack(PrecisionAssemblerContent.ITEM.get()));}
        @Override public void draw(PrecisionAssemblerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){PrecisionAssemblerViews.draw(graphics,recipe,0,0);}
    }
}
