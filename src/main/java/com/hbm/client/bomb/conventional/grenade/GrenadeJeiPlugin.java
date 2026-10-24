package com.hbm.client.bomb.conventional.grenade;
import com.hbm.bomb.conventional.grenade.*;
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
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
/** Exact source GrenadeRecipeHandler recipes/usages through JEI, with identity limited to actual grenade parts. */
@JeiPlugin
public final class GrenadeJeiPlugin implements IModPlugin{
 static final RecipeType<GrenadeRecipes.Assembly> TYPE=RecipeType.create("hbm","grenade_assembly",GrenadeRecipes.Assembly.class);
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:grenade_jei");}
 @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
 @Override public void registerRecipes(IRecipeRegistration event){event.addRecipes(TYPE,GrenadeRecipes.all());}
 @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){event.addRecipeCatalysts(TYPE,Items.CRAFTING_TABLE);}
 @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event){event.addRecipeTransferHandler(CraftingMenu.class,MenuType.CRAFTING,TYPE,1,9,10,36);event.addRecipeTransferHandler(InventoryMenu.class,null,TYPE,1,4,9,36);}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,GrenadeContent.creativeStacks().stream().skip(1).toList());}
 @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(GrenadeContent.UNIVERSAL.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return GrenadeRecipes.key(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Long.toString(GrenadeRecipes.key(stack));}});}
 private static final class Category implements IRecipeCategory<GrenadeRecipes.Assembly>{
  final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(Items.CRAFTING_TABLE));}
  @Override public RecipeType<GrenadeRecipes.Assembly> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("hbm.grenades.assembly");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
  @Override public void setRecipe(IRecipeLayoutBuilder builder,GrenadeRecipes.Assembly recipe,IFocusGroup focuses){var points=GrenadeRecipeDrawing.inputs(recipe.inputs().size());for(int i=0;i<points.length;i++)builder.addSlot(RecipeIngredientRole.INPUT,points[i][0],points[i][1]).addItemStack(recipe.inputs().get(i));builder.addSlot(RecipeIngredientRole.OUTPUT,102,24).addItemStack(recipe.output());builder.addSlot(RecipeIngredientRole.CATALYST,75,31).addItemStack(new ItemStack(Items.CRAFTING_TABLE));}
  @Override public void draw(GrenadeRecipes.Assembly recipe,IRecipeSlotsView slots,GuiGraphics graphics,double mouseX,double mouseY){GrenadeRecipeDrawing.draw(graphics,recipe.inputs().size(),0,0);}
 }
}
