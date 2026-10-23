package com.hbm.client.oilmixer;
import com.hbm.machine.oilmixer.*;
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
@JeiPlugin public final class MixerJeiPlugin implements IModPlugin {
 static final RecipeType<MixerRecipe> TYPE=RecipeType.create("hbm","oil_mixer",MixerRecipe.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:oil_mixer_jei");}@Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration e){var level=Minecraft.getInstance().level;if(level!=null)e.addRecipes(TYPE,MixerRecipeConfig.recipes(level.getRecipeManager()));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,MixerContent.ITEM.get());}@Override public void registerGuiHandlers(IGuiHandlerRegistration e){e.addRecipeClickArea(MixerScreen.class,66,20,52,44,TYPE);}
 private static final class Category implements IRecipeCategory<MixerRecipe>{final IDrawable background,icon;Category(IGuiHelper h){background=h.createBlankDrawable(166,65);icon=h.createDrawableItemStack(new ItemStack(MixerContent.ITEM.get()));}@Override public RecipeType<MixerRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineMixer");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder b,MixerRecipe r,IFocusGroup focus){var inputs=MixerViews.inputs(r);for(int i=0;i<inputs.size();i++)b.addSlot(RecipeIngredientRole.INPUT,21+i*18,24).addItemStacks(inputs.get(i)).addTooltipCallback((slot,tooltip)->slot.getDisplayedItemStack().ifPresent(stack->tooltip.addAll(com.hbm.ore.OreRecipeInputs.tooltip(stack))));b.addSlot(RecipeIngredientRole.OUTPUT,113,24).addItemStack(MixerViews.output(r));b.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(MixerContent.ITEM.get()));}@Override public void draw(MixerRecipe r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){MixerViews.draw(r,g,0,0);}
 }
}
