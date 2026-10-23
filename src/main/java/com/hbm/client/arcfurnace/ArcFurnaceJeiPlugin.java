package com.hbm.client.arcfurnace;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.arcfurnace.*;
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
/** Both original NEI categories, electrode/sand metadata and genuine machine catalyst. */
@JeiPlugin
public final class ArcFurnaceJeiPlugin implements IModPlugin {
 static final RecipeType<ArcFurnaceViews.View> SOLID=RecipeType.create("hbm","arc_furnace_solid",ArcFurnaceViews.View.class),LIQUID=RecipeType.create("hbm","arc_furnace_liquid",ArcFurnaceViews.View.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:arc_furnace_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){var helper=event.getJeiHelpers().getGuiHelper();event.addRecipeCategories(new Category(helper,false),new Category(helper,true));}
 @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null){event.addRecipes(SOLID,ArcFurnaceViews.recipes(level.getRecipeManager(),false));event.addRecipes(LIQUID,ArcFurnaceViews.recipes(level.getRecipeManager(),true));}}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(SOLID,ArcFurnaceContent.ITEM.get());event.addRecipeCatalysts(LIQUID,ArcFurnaceContent.ITEM.get());}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){for(var item:java.util.List.of(ArcFurnaceContent.ELECTRODE.get(),ArcFurnaceContent.BURNT.get(),ArcFurnaceContent.SAND_ITEM.get()))event.registerSubtypeInterpreter(item,new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){var entries=new java.util.ArrayList<ItemStack>();entries.addAll(ArcFurnaceContent.creativeStacks("machine"));entries.addAll(ArcFurnaceContent.creativeStacks("control"));event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,entries);}
 static final class Category implements IRecipeCategory<ArcFurnaceViews.View>{
  final boolean liquid;final IDrawable background,icon;Category(IGuiHelper helper,boolean liquid){this.liquid=liquid;background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(ArcFurnaceContent.ITEM.get()));}
  @Override public RecipeType<ArcFurnaceViews.View> getRecipeType(){return liquid?LIQUID:SOLID;}@Override public Component getTitle(){return Component.translatable("block.hbm.machine_arc_furnace");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,ArcFurnaceViews.View view,IFocusGroup focuses){builder.addSlot(RecipeIngredientRole.INPUT,48,24).addItemStacks(view.inputs());var out=ArcFurnaceViews.outputCoords(view);for(int i=0;i<view.outputs().size();i++)builder.addSlot(RecipeIngredientRole.OUTPUT,out[i][0],out[i][1]).addItemStack(view.outputs().get(i));builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(ArcFurnaceContent.ITEM.get()));}
  @Override public void draw(ArcFurnaceViews.View view,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){ArcFurnaceViews.draw(graphics,view,0,0);}
 }
}
