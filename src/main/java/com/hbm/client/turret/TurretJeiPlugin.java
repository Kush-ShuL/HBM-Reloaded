package com.hbm.client.turret;
import com.hbm.machine.turret.TurretContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
/** Real native workbench recipes are displayed by JEI's vanilla category; expose all actual source creative items. */
@JeiPlugin public final class TurretJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:sentry_turrets");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,TurretContent.creativeStacks("weapon"));}
}
