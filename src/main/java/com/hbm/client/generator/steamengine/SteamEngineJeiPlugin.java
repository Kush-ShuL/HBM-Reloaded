package com.hbm.client.generator.steamengine;
import com.hbm.machine.generator.steamengine.*;
import mezz.jei.api.*;
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
public final class SteamEngineJeiPlugin implements IModPlugin {
    private static final RecipeType<SteamEngineViews.View> TYPE=RecipeType.create("hbm","piston_steam_engine",SteamEngineViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:piston_steam_engine_jei");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,SteamEngineViews.recipes());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,SteamEngineContent.ITEM.get());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(SteamEngineContent.GEAR.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return LargeGearItem.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,SteamEngineContent.GEAR.get().creativeStacks());}
    private static final class Category implements IRecipeCategory<SteamEngineViews.View> {private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(SteamEngineContent.ITEM.get()));}@Override public RecipeType<SteamEngineViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_steam_engine");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}@Override public void setRecipe(IRecipeLayoutBuilder builder,SteamEngineViews.View recipe,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(SteamEngineContent.ITEM.get()));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());}@Override public void draw(SteamEngineViews.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){SteamEngineViews.draw(graphics,0,0,recipe.energy());}}
}
