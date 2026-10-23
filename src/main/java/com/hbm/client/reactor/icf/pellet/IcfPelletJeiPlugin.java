package com.hbm.client.reactor.icf.pellet;
import com.hbm.machine.reactor.icf.pellet.*;
import com.hbm.registry.HbmContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class IcfPelletJeiPlugin implements IModPlugin {
 private static final RecipeType<IcfPelletViews.Row> TYPE=RecipeType.create("hbm","icf_pellet_press",IcfPelletViews.Row.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:icf_pellet_press_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
 @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,IcfPelletViews.rows());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,IcfPelletPressContent.ITEM.get());}
 @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(IcfPelletPressScreen.class,98,36,16,14,TYPE);}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(IcfPelletPressContent.PELLET.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return IcfPelletViews.subtype(stack);}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return IcfPelletViews.subtype(stack);}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,IcfPelletItem.creativeStacks());}
 private static final class Category implements IRecipeCategory<IcfPelletViews.Row> {
  private final IDrawable background,icon;Category(IGuiHelper gui){background=gui.createBlankDrawable(166,65);icon=gui.createDrawableItemStack(new ItemStack(IcfPelletPressContent.ITEM.get()));}
  @Override public RecipeType<IcfPelletViews.Row> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.machineICFPress");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,IcfPelletViews.Row row,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,21,24).addItemStacks(row.first());builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(row.second());builder.addSlot(RecipeIngredientRole.INPUT,48,6).addItemStack(new ItemStack(HbmContent.item("icf_pellet_empty")));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(row.output());if(row.muon())builder.addSlot(RecipeIngredientRole.CATALYST,75,24).addItemStack(new ItemStack(HbmContent.item("particle_muon")));}
  @Override public void draw(IcfPelletViews.Row row,IRecipeSlotsView slots,GuiGraphics g,double x,double y){IcfPelletViews.draw(g,row,0,0);}
 }
}
