package com.hbm.compat.jei;

import com.hbm.HbmReloaded;
import com.hbm.item.battery.BatteryContent;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import com.hbm.client.gui.ElectricFurnaceScreen;
import com.hbm.machine.electricfurnace.ElectricFurnaceContent;
import com.hbm.machine.electricfurnace.ElectricFurnaceMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

/** Optional JEI entry point: real smelting catalyst, old progress hitbox and input transfer. */
@JeiPlugin
public final class HbmJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return HbmReloaded.id("jei"); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(RecipeTypes.SMELTING, ElectricFurnaceContent.ITEM.get());
    }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        com.hbm.registry.HbmOres.allItems().stream().map(holder -> holder.get())
                .filter(item -> item instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
                .map(item -> (com.hbm.block.ore.LegacyOreItem) item).forEach(item -> subtype(registration, item, item::ordinal));
        for (var battery : List.of(BatteryContent.CUBE_POWER.get(), BatteryContent.POTATO.get(), BatteryContent.MEMORY.get())) {
            subtype(registration, battery, battery::getCharge);
        }
        for(var pack:BatteryContent.packItems())subtype(registration,pack,stack->pack.getCharge(stack));
        com.hbm.item.fluid.FluidItemContent.subtypedItems().forEach(item -> subtype(registration, item, stack -> com.hbm.item.fluid.FluidItemContent.type(stack).getID()));
        com.hbm.block.pipe.PipeContent.subtypeItems().forEach(item -> subtype(registration, item, HbmJeiPlugin::metadata));
        subtype(registration, com.hbm.block.pipe.PipeContent.TYPED_ITEM.get(), stack -> com.hbm.item.fluid.FluidItemContent.type(stack).getID());
    }
    private static int metadata(ItemStack stack) { return stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), 0); }
    private static void subtype(ISubtypeRegistration registration, net.minecraft.world.item.Item item, java.util.function.Function<ItemStack, Object> data) {
        registration.registerSubtypeInterpreter(item, new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return data.apply(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return data.apply(stack).toString(); }
        });
    }
    @Override public void registerExtraIngredients(mezz.jei.api.registration.IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, BatteryContent.selfChargingStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, BatteryContent.packCreativeStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.registry.HbmOres.drillbitStacks());
        var variants = com.hbm.registry.HbmEnumItems.tabNames().stream().flatMap(tab -> com.hbm.registry.HbmEnumItems.creativeStacks(tab).stream()).toList();
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, variants);
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.item.fluid.FluidIconItem.recipeViewerStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.registry.HbmOres.meteorStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.compat.recipeviewer.RecipeViewerVisibility.secretStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.bomb.nuclear.NuclearContent.breedingRodStacks());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, com.hbm.registry.HbmOres.allItems().stream().map(holder -> holder.get())
                .filter(item -> item instanceof com.hbm.block.ore.LegacyOreItem ore && ore.spec().multi())
                .flatMap(item -> ((com.hbm.block.ore.LegacyOreItem) item).creativeStacks().stream()).toList());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, List.of("parts", "control", "template").stream().flatMap(tab -> com.hbm.item.fluid.FluidItemContent.creativeStacks(tab).stream()).toList());
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK, List.of("machine", "template", "parts", "block").stream().flatMap(tab -> com.hbm.block.pipe.PipeContent.creativeStacks(tab).stream()).toList());
    }
    @Override public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(
                new ItemStack(ElectricFurnaceContent.ON_ITEM.get()),
                new ItemStack(com.hbm.item.fluid.FluidItemContent.ICON.get()),
                BatteryContent.MEMORY.get().emptyStack(), BatteryContent.MEMORY.get().fullStack()));
        if (com.hbm.compat.recipeviewer.RecipeViewerVisibility.hideSecretsEnabled()) {
            var manager = runtime.getIngredientManager();
            var secrets = manager.getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                    .filter(com.hbm.compat.recipeviewer.RecipeViewerVisibility::secretEntry).toList();
            if (!secrets.isEmpty()) manager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, secrets);
        }
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ElectricFurnaceScreen.class, 43, 36, 28, 12, RecipeTypes.SMELTING);
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ElectricFurnaceMenu.class, ElectricFurnaceContent.MENU.get(), RecipeTypes.SMELTING, 1, 1, 4, 36);
    }
}
