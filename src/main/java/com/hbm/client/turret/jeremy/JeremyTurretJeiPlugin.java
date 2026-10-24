package com.hbm.client.turret.jeremy;
import com.hbm.machine.turret.jeremy.JeremyTurretContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class JeremyTurretJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:jeremy_turrets");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,JeremyTurretContent.creativeStacks("weapon"));}
}
