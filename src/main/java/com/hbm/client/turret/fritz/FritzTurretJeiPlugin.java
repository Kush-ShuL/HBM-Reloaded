package com.hbm.client.turret.fritz;
import com.hbm.machine.turret.fritz.FritzTurretContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class FritzTurretJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:fritz_turrets");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,FritzTurretContent.creativeStacks("weapon"));}
}
