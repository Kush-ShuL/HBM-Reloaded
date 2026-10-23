package com.hbm.machine.reactor.zirnox.client;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.reactor.zirnox.*;
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
public final class ZirnoxJeiPlugin implements IModPlugin {
    static final RecipeType<ZirnoxFuelRecipes> TYPE=RecipeType.create("hbm","zirnox",ZirnoxFuelRecipes.class);
    @Override public ResourceLocation getPluginUid(){return ZirnoxRenderer.id("zirnox_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(ZirnoxContent.ROD.get(),new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,ZirnoxContent.ROD.get().creativeStacks());}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,ZirnoxFuelRecipes.all());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,ZirnoxContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<ZirnoxFuelRecipes>{
        private final IDrawable background,icon;
        Category(IGuiHelper helper){background=helper.createBlankDrawable(166,60);icon=helper.createDrawableItemStack(new ItemStack(ZirnoxContent.ITEM.get()));}
        @Override public RecipeType<ZirnoxFuelRecipes> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("tile.machine_zirnox.name");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,ZirnoxFuelRecipes recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStack(recipe.input());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(ZirnoxContent.ITEM.get()));}
        @Override public void draw(ZirnoxFuelRecipes recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ZirnoxFuelRecipeView.draw(graphics,0,0);}
    }
}
