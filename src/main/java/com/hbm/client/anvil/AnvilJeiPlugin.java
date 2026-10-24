package com.hbm.client.anvil;
import com.hbm.machine.anvil.*;
import java.util.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.*;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
@JeiPlugin
public final class AnvilJeiPlugin implements IModPlugin{
    static final RecipeType<AnvilViews.View> TYPE=RecipeType.create("hbm","ntm_anvil",AnvilViews.View.class);
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.fromNamespaceAndPath("hbm","anvil_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){var variants=new ArrayList<ItemStack>(MoldContent.creativeStacks());for(var holder:AnvilContent.HOT){int count=holder.get().symbol().equals("ingot_steel_dusted")?10:1;for(int i=0;i<count;i++){var stack=new ItemStack(holder.get());stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),i);variants.add(stack);}}event.addExtraIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK,variants);}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){var items=new ArrayList<net.minecraft.world.item.Item>();AnvilContent.HOT.forEach(holder->items.add(holder.get()));for(var item:items)event.registerSubtypeInterpreter(item,new mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,mezz.jei.api.ingredients.subtypes.UidContext context){return getSubtypeData(stack,context).toString();}});}
    @Override public void registerCategories(IRecipeCategoryRegistration event){event.addRecipeCategories(new Category(event.getJeiHelpers().getGuiHelper()));}
    @Override public void registerRecipes(IRecipeRegistration event){var level=Minecraft.getInstance().level;if(level!=null)event.addRecipes(TYPE,AnvilViews.recipes(level.getRecipeManager()));}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration event){for(var block:AnvilContent.ANVILS.values())event.addRecipeCatalysts(TYPE,block.get());}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration event){var helper=event.getTransferHelper();var basic=helper.createUnregisteredRecipeTransferHandler(helper.createBasicRecipeTransferInfo(AnvilMenu.class,AnvilContent.MENU.get(),TYPE,0,2,3,36));event.addRecipeTransferHandler(new IRecipeTransferHandler<AnvilMenu,AnvilViews.View>(){
        @Override public Class<AnvilMenu> getContainerClass(){return AnvilMenu.class;}@Override public Optional<MenuType<AnvilMenu>> getMenuType(){return Optional.of(AnvilContent.MENU.get());}@Override public RecipeType<AnvilViews.View> getRecipeType(){return TYPE;}
        @Override public IRecipeTransferError transferRecipe(AnvilMenu menu,AnvilViews.View view,IRecipeSlotsView slots,Player player,boolean maximum,boolean transfer){if(!view.tierValid(menu.tier()))return helper.createUserErrorWithTooltip(Component.translatable("gui.hbm.anvil.wrong_tier"));if(view.smithing())return basic.transferRecipe(menu,view,slots,player,maximum,transfer);if(transfer)menu.focusConstruction(view.id());return null;}
    },TYPE);}
    private static final class Category implements IRecipeCategory<AnvilViews.View>{
        final IDrawable background,icon;Category(IGuiHelper helper){background=helper.createBlankDrawable(166,65);icon=helper.createDrawableItemStack(new ItemStack(AnvilContent.ANVILS.get("iron").get()));}
        @Override public RecipeType<AnvilViews.View> getRecipeType(){return TYPE;}@Override public Component getTitle(){return Component.translatable("gui.hbm.anvil.title");}@Override public IDrawable getBackground(){return background;}@Override public IDrawable getIcon(){return icon;}
        @Override public void setRecipe(IRecipeLayoutBuilder builder,AnvilViews.View view,IFocusGroup focuses){var p=AnvilViews.layout(view);for(int i=0;i<view.inputs().size();i++)builder.addSlot(RecipeIngredientRole.INPUT,p.ix()+18*(i%p.inColumns()),p.iy()+18*(i/p.inColumns())).addItemStacks(view.inputs().get(i)).addTooltipCallback((slot,tooltip)->tooltip.addAll(AnvilViews.conditions(view)));for(int i=0;i<view.outputs().size();i++){var output=view.outputs().get(i);builder.addSlot(RecipeIngredientRole.OUTPUT,p.ox()+18*(i%p.outColumns()),p.oy()+18*(i/p.outColumns())).addItemStack(output.stack()).addTooltipCallback((slot,tooltip)->{if(output.chance()!=1)tooltip.add(Component.literal(AnvilViews.chance(output.chance())).withStyle(net.minecraft.ChatFormatting.RED));});}builder.addSlot(RecipeIngredientRole.CATALYST,p.anvilX(),31).addItemStacks(AnvilContent.exactTier(view.lower()));}
        @Override public void draw(AnvilViews.View view,IRecipeSlotsView slots,GuiGraphics graphics,double x,double y){AnvilViews.draw(graphics,view,0,0);}
    }
}
