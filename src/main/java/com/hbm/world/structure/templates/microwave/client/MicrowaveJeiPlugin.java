package com.hbm.world.structure.templates.microwave.client;
import com.hbm.world.structure.templates.microwave.*;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class MicrowaveJeiPlugin implements IModPlugin {
    static final RecipeType<MicrowaveRecipes.View> TYPE=RecipeType.create("hbm","microwave",MicrowaveRecipes.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:microwave_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,MicrowaveRecipes.recipes());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,MicrowaveContent.ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(MicrowaveScreen.class,104,34,22,16,TYPE);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event){event.addRecipeTransferHandler(new IRecipeTransferInfo<MicrowaveMenu,MicrowaveRecipes.View>(){
        @Override public Class<MicrowaveMenu> getContainerClass(){return MicrowaveMenu.class;}
        @Override public Optional<MenuType<MicrowaveMenu>> getMenuType(){return Optional.of(MicrowaveContent.MENU.get());}
        @Override public RecipeType<MicrowaveRecipes.View> getRecipeType(){return TYPE;}
        @Override public boolean canHandle(MicrowaveMenu menu,MicrowaveRecipes.View recipe){return true;}
        @Override public List<Slot> getRecipeSlots(MicrowaveMenu menu,MicrowaveRecipes.View recipe){return List.of(menu.getSlot(0));}
        @Override public List<Slot> getInventorySlots(MicrowaveMenu menu,MicrowaveRecipes.View recipe){return menu.slots.subList(3,39);}
    });}
    private static final class Category implements IRecipeCategory<MicrowaveRecipes.View>{
        private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(88,34);icon=helper.createDrawableItemStack(new ItemStack(MicrowaveContent.ITEM.get()));}
        @Override public RecipeType<MicrowaveRecipes.View> getRecipeType(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.microwave");}
        @Override public IDrawable getBackground(){return background;}
        @Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,MicrowaveRecipes.View recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,8,9).addItemStacks(recipe.inputs());builder.addSlot(RecipeIngredientRole.OUTPUT,68,9).addItemStack(recipe.output());}
        @Override public void draw(MicrowaveRecipes.View recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){MicrowaveRecipes.draw(graphics,0,0);}
    }
}
