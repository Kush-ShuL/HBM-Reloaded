package com.hbm.client.weapontable;
import com.hbm.machine.weapontable.WeaponTableContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
/** The table's real native workbench recipe is handled by JEI's vanilla category. */
@JeiPlugin
public final class WeaponTableJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:weapon_table_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,WeaponTableContent.indexedItems());}
}
