package com.hbm.client.woodprocessing;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.woodprocessing.WoodProcessingContent;
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
@JeiPlugin public final class WoodJeiPlugin implements IModPlugin {
    private static final RecipeType<SawmillViews.View> TYPE=RecipeType.create("hbm","sawmill",SawmillViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:wood_processing54");}@Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}@Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,SawmillViews.views());}@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,WoodProcessingContent.SAWMILL_ITEM.get());}@Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,WoodProcessingContent.allStacks());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(WoodProcessingContent.SAWMILL_ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
    private static final class Category implements IRecipeCategory<SawmillViews.View>{private final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(WoodProcessingContent.SAWMILL_ITEM.get()));}@Override public RecipeType<SawmillViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_sawmill");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,SawmillViews.View view,IFocusGroup focus){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(view.inputs());builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(view.result());if(view.dustChance()>0)builder.addSlot(RecipeIngredientRole.OUTPUT,120,24).addItemStack(SawmillViews.dust()).addTooltipCallback((slot,tooltip)->tooltip.add(SawmillViews.chance(view)));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(WoodProcessingContent.SAWMILL_ITEM.get()));}
        @Override public void draw(SawmillViews.View view,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){SawmillViews.draw(graphics,view,0,0);}
    }
}
