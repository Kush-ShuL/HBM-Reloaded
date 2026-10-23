package com.hbm.client.assembler;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.assembler.*;
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
public final class AssemblerJeiPlugin implements IModPlugin {
    static final RecipeType<AssemblerRecipe> TYPE=RecipeType.create("hbm","assembly",AssemblerRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","assembly_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,AssemblerRecipeConfig.recipes(level.getRecipeManager()).stream().filter(AssemblerViews::visible).toList());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,AssemblerContent.ITEM.get());}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,Blueprints.creativeStacks());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(Blueprints.ITEM.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){String pool=Blueprints.pool(stack);return pool==null?"":pool;}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});event.registerSubtypeInterpreter(Blueprints.FOLDER.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return Blueprints.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
    private static final class Category implements IRecipeCategory<AssemblerRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(AssemblerContent.ITEM.get()));}
        @Override public RecipeType<AssemblerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineAssemblyMachine");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,AssemblerRecipe recipe,IFocusGroup focuses){var display=AssemblerViews.drawing(recipe);var in=AssemblerViews.inputs(recipe);var out=AssemblerViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(AssemblerViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(AssemblerViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(AssemblerViews.outputTooltip(recipe,lane,stack))));}int offset=AssemblerViews.offset(recipe);if(recipe.isPooled())builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,10).addItemStacks(AssemblerViews.blueprints(recipe));builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,recipe.isPooled()?38:31).addItemStack(new ItemStack(AssemblerContent.ITEM.get()));}
        @Override public void draw(AssemblerRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){AssemblerViews.draw(graphics,recipe,0,0);}
    }
}
