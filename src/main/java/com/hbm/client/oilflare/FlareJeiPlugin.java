package com.hbm.client.oilflare;
import com.hbm.machine.oilflare.FlareContent;
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
/** Source operation information for all actual gas/flammable types, plus canonical assembly manufacture elsewhere. */
@JeiPlugin public final class FlareJeiPlugin implements IModPlugin {
    static final RecipeType<FlareViews.Operation> TYPE=RecipeType.create("hbm","gas_flare",FlareViews.Operation.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:gas_flare_jei");}
    @Override public void registerCategories(IRecipeCategoryRegistration e){e.addRecipeCategories(new Category(e.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration e){e.addRecipes(TYPE,FlareViews.operations());}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration e){e.addRecipeCatalysts(TYPE,FlareContent.ITEM.get());}
    private static final class Category implements IRecipeCategory<FlareViews.Operation>{private final IDrawable background,icon;Category(IGuiHelper h){background=h.createBlankDrawable(166,96);icon=h.createDrawableItemStack(new ItemStack(FlareContent.ITEM.get()));}
        @Override public RecipeType<FlareViews.Operation> getRecipeType(){return TYPE;}
        @Override public Component getTitle(){return Component.translatable("container.gasFlare");}
        @Override public IDrawable getBackground(){return background;}
        @Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder b,FlareViews.Operation r,IFocusGroup focus){b.addSlot(RecipeIngredientRole.INPUT,8,8).addItemStack(r.icon());b.addSlot(RecipeIngredientRole.CATALYST,36,8).addItemStack(new ItemStack(FlareContent.ITEM.get()));}
        @Override public void draw(FlareViews.Operation r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){var font=com.hbm.client.gui.font.LegacyFonts.get();Component[] lines={FlareViews.mode(r),FlareViews.rate(r),FlareViews.output(r),FlareViews.efficiency(r),Component.translatable("gui.hbm.flare.upgrades")};for(int i=0;i<lines.length;i++)g.drawString(font,lines[i],3,36+i*11,0x404040,false);}
    }
}
