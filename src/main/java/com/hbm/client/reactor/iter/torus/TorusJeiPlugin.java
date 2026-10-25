package com.hbm.client.reactor.iter.torus;
import com.hbm.compat.recipeviewer.LegacyRecipeDisplays;
import com.hbm.machine.reactor.iter.*;
import com.hbm.machine.reactor.iter.torus.*;
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
public final class TorusJeiPlugin implements IModPlugin {
    static final RecipeType<FusionRecipe> TYPE=RecipeType.create("hbm","fusion",FusionRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","fusion_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()),new Tooling(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,FusionRecipeConfig.recipes(level.getRecipeManager()).stream().filter(TorusViews::visible).toList());event.addRecipes(TOOLING,List.of(Boolean.TRUE));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,TorusContent.ITEM.get());for(var tool:TorusTooling.tools())event.addRecipeCatalyst(tool,TOOLING);}
    private static final class Category implements IRecipeCategory<FusionRecipe>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(TorusContent.ITEM.get()));}
        @Override public RecipeType<FusionRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.fusionTorus");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,FusionRecipe recipe,IFocusGroup focuses){var display=TorusViews.drawing(recipe);var in=TorusViews.inputs(recipe);var out=TorusViews.outputs(recipe);var ip=LegacyRecipeDisplays.inputCoordinates(TorusViews.CATEGORY,in.size());var op=LegacyRecipeDisplays.outputCoordinates(TorusViews.CATEGORY,display);for(int i=0;i<in.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,ip[i][0],ip[i][1]).addItemStacks(in.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));for(int i=0;i<out.size();i++){int lane=i;builder.addSlot(RecipeIngredientRole.OUTPUT,op[i][0],op[i][1]).addItemStacks(out.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(TorusViews.outputTooltip(recipe,lane,stack))));}int offset=TorusViews.offset(recipe);builder.addSlot(RecipeIngredientRole.CATALYST,75+offset,31).addItemStack(new ItemStack(TorusContent.ITEM.get()));}
        @Override public void draw(FusionRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){TorusViews.draw(graphics,recipe,0,0);}
    }

 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){var stacks=new ArrayList<ItemStack>(TorusContent.driveStacks());stacks.addAll(TorusContent.componentStacks());event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,stacks);}
 static final RecipeType<Boolean> TOOLING=RecipeType.create("hbm","fusion_tooling",Boolean.class);
 static final class Tooling implements IRecipeCategory<Boolean>{final IDrawable background,icon;Tooling(IGuiHelper gui){background=gui.createBlankDrawable(166,65);icon=gui.createDrawableItemStack(TorusTooling.output());}public RecipeType<Boolean> getRecipeType(){return TOOLING;}public Component getTitle(){return Component.translatable("recipe.hbm.fusion_tooling");}public IDrawable getBackground(){return background;}public IDrawable getIcon(){return icon;}public void setRecipe(IRecipeLayoutBuilder layout,Boolean recipe,IFocusGroup focus){layout.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(TorusTooling.bolts());layout.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(TorusTooling.input());layout.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(TorusTooling.tools());layout.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(TorusTooling.output());}public void draw(Boolean recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){TorusToolingViews.draw(graphics,0,0);graphics.blit(ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),29,23,5,87,18,18,256,256);}}

}
