package com.hbm.client.pedestal;
import com.hbm.machine.pedestal.*;
import com.hbm.hazard.HbmHazards;
import java.util.List;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class PedestalJeiPlugin implements IModPlugin {
    public static final RecipeType<PedestalRecipe> TYPE=RecipeType.create("hbm","pedestal",PedestalRecipe.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pedestal_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration event){if(!PedestalViewerConfig.showRecipesInViewers())return;event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){if(!PedestalViewerConfig.showRecipesInViewers())return;var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,PedestalRecipeConfig.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){if(!PedestalViewerConfig.showRecipesInViewers())return;event.addRecipeCatalysts(TYPE,PedestalContent.ITEM.get(),PedestalContent.TABLET.get());}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,PedestalContent.indexedItems());}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(PedestalContent.TABLET.get(),new ISubtypeInterpreter<ItemStack>(){public Object getSubtypeData(ItemStack stack,UidContext context){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}
    private static final class Category implements IRecipeCategory<PedestalRecipe>{
        private final IDrawable background,icon;Category(IGuiHelper gui){background=gui.createBlankDrawable(142,114);icon=gui.createDrawableItemStack(new ItemStack(PedestalContent.ITEM.get()));}
        @Override public RecipeType<PedestalRecipe> getRecipeType(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("block.hbm.pedestal");}
        @Override public IDrawable getBackground(){return background;}
        @Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,PedestalRecipe recipe,IFocusGroup focuses){for(int i=0;i<9;i++)if(recipe.inputs().get(i).count()>0)builder.addSlot(RecipeIngredientRole.INPUT,7+i%3*27,37+i/3*27).addItemStacks(recipe.inputs().get(i).examples());builder.addSlot(RecipeIngredientRole.OUTPUT,63,10).addItemStack(recipe.result());}
        @Override public void draw(PedestalRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){graphics.blit(ClayTabletScreen.TEXTURE,0,30,0,0,142,84,256,256);for(int i=0;i<9;i++)if(recipe.inputs().get(i).count()==0)graphics.blit(ClayTabletScreen.TEXTURE,7+i%3*27,37+i/3*27,142,0,16,16,256,256);int condition=switch(recipe.extra()){case FULL_MOON->32;case NEW_MOON->48;case SUN->64;default->-1;};if(condition>=0)graphics.blit(ClayTabletScreen.TEXTURE,120,92,142,condition,16,16,256,256);}
        @Override public List<Component> getTooltipStrings(PedestalRecipe recipe,IRecipeSlotsView slots,double x,double y){return recipe.extra()==PedestalRecipe.Extra.NONE?List.of():List.of(Component.translatable("gui.hbm.pedestal.condition."+recipe.extra().name().toLowerCase(java.util.Locale.ROOT)));}
    }
}
