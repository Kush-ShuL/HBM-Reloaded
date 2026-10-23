package com.hbm.machine.reactor.client;
import com.hbm.machine.reactor.*;
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
public final class ReactorJeiPlugin implements IModPlugin {
    static final RecipeType<BreedingRecipe> TYPE=RecipeType.create("hbm","research_breeding",BreedingRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","research_reactors_jei");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){
        for(var item:ReactorContent.wasteItems())event.registerSubtypeInterpreter(item,new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){
            @Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0);}
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}
        });
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){var entries=new java.util.ArrayList<ItemStack>();for(var item:ReactorContent.fuelItems())entries.add(new ItemStack(item));for(var item:ReactorContent.wasteItems())for(int metadata=0;metadata<2;metadata++){var stack=new ItemStack(item);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),metadata);entries.add(stack);}event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,entries);}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,BreedingRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,ReactorContent.BREEDING_ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration event){event.addRecipeClickArea(BreedingReactorScreen.class,53,32,70,20,TYPE);}
    private static final class Category implements IRecipeCategory<BreedingRecipe>{
        private final IDrawable background,icon;
        Category(IGuiHelper helper){background=helper.createBlankDrawable(166,60);icon=helper.createDrawableItemStack(new ItemStack(ReactorContent.BREEDING_ITEM.get()));}
        @Override public RecipeType<BreedingRecipe> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("container.reactorBreeding");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,BreedingRecipe recipe,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,30,24).addIngredients(recipe.input());builder.addSlot(RecipeIngredientRole.OUTPUT,120,24).addItemStack(recipe.result());}
        @Override public void draw(BreedingRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double mouseX,double mouseY){BreedingRecipeView.draw(graphics,recipe,0,0);}
    }
}
