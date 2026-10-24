package com.hbm.client.machine.pa.parts;
import com.hbm.machine.pa.parts.PaPartsContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
/** Fixed physical PA coil items use the ordinary item identity. */
@JeiPlugin public final class PaPartsJeiPlugin implements IModPlugin{
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:pa_parts_jei");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,PaPartsContent.coilStacks());}
}
