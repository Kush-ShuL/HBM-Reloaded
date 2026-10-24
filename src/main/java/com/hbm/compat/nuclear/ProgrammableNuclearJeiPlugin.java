package com.hbm.compat.nuclear;
import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.bomb.nuclear.programmable.*;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** The 21 fixed circuit items and actual bomb-loading requirements/formulae, beside native manufacturing recipes. */
@JeiPlugin
public final class ProgrammableNuclearJeiPlugin implements IModPlugin {
 private static final RecipeType<NuclearPayloadDisplays.Display> TYPE=RecipeType.create("hbm","nuclear_payload",NuclearPayloadDisplays.Display.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:programmable_nuclear");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,SourceCircuitItem.creativeStacks());}
 @Override public void registerCategories(IRecipeCategoryRegistration registration){var helper=registration.getJeiHelpers().getGuiHelper();registration.addRecipeCategories(new IRecipeCategory<NuclearPayloadDisplays.Display>(){
  public RecipeType<NuclearPayloadDisplays.Display> getRecipeType(){return TYPE;}public Component getTitle(){return Component.translatable("gui.hbm.nuclear_payload");}public IDrawable getBackground(){return helper.createBlankDrawable(176,84);}public IDrawable getIcon(){return helper.createDrawableItemStack(new ItemStack(ProgrammableNuclearContent.CUSTOM_ITEM.get()));}
  public void setRecipe(IRecipeLayoutBuilder builder,NuclearPayloadDisplays.Display display,IFocusGroup focuses){var inputs=display.inputs();for(int i=0;i<inputs.size();i++)builder.addSlot(RecipeIngredientRole.INPUT,0,i*20).addItemStacks(inputs.get(i));builder.addSlot(RecipeIngredientRole.CATALYST,158,0).addItemStack(display.bomb());}
  public void draw(NuclearPayloadDisplays.Display display,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){NuclearPayloadDrawing.draw(graphics,display,0,0);}
 });}
 @Override public void registerRecipes(IRecipeRegistration registration){registration.addRecipes(TYPE,NuclearPayloadDisplays.all());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalyst(new ItemStack(ProgrammableNuclearContent.TIMED_ITEM.get()),TYPE);registration.addRecipeCatalyst(new ItemStack(ProgrammableNuclearContent.CUSTOM_ITEM.get()),TYPE);}
 @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(CustomNuclearScreen.class,16,89,144,18,TYPE);registration.addRecipeClickArea(TimedBalefireScreen.class,19,90,30,16,TYPE);}
}
