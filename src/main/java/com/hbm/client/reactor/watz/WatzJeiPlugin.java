package com.hbm.client.reactor.watz;
import com.hbm.machine.reactor.watz.WatzContent;
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
public final class WatzJeiPlugin implements IModPlugin {
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(WatzContent.CASING.get().asItem(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0);}public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,java.util.List.of(WatzContent.creativeStacks("machine").getLast()));}
 static final RecipeType<WatzViews.View> TYPE=RecipeType.create("hbm","watz",WatzViews.View.class);@Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","watz_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()),new Tooling(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,WatzViews.recipes());event.addRecipes(TOOLING,java.util.List.of(Boolean.TRUE));}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,WatzContent.REACTOR.get());for(var tool:WatzTooling.tools())event.addRecipeCatalyst(tool,TOOLING);}
 static final class Category implements IRecipeCategory<WatzViews.View>{final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(WatzContent.REACTOR.get()));}@Override public RecipeType<WatzViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.watz");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,WatzViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.fresh());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(WatzContent.REACTOR.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.depleted());}@Override public void draw(WatzViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){WatzViews.draw(graphics,0,0);}}
 static final RecipeType<Boolean> TOOLING=RecipeType.create("hbm","watz_tooling",Boolean.class);
 static final class Tooling implements IRecipeCategory<Boolean>{final IDrawable background,icon;Tooling(IGuiHelper gui){background=gui.createBlankDrawable(166,65);icon=gui.createDrawableItemStack(WatzTooling.output());}public RecipeType<Boolean> getRecipeType(){return TOOLING;}public Component getTitle(){return Component.translatable("recipe.hbm.watz_tooling");}public IDrawable getBackground(){return background;}public IDrawable getIcon(){return icon;}public void setRecipe(IRecipeLayoutBuilder layout,Boolean recipe,IFocusGroup focus){layout.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(WatzTooling.bolts());layout.addSlot(RecipeIngredientRole.INPUT,30,24).addItemStack(WatzTooling.input());layout.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStacks(WatzTooling.tools());layout.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(WatzTooling.output());}public void draw(Boolean recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){WatzViews.draw(graphics,0,0);graphics.blit(ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png"),29,23,5,87,18,18,256,256);}}

}
