package com.hbm.client.rtgblast;

import com.hbm.client.alloy.AlloyJeiPlugin;
import com.hbm.client.rtg.RtgJeiPlugin;
import com.hbm.machine.rtgblast.*;
import mezz.jei.api.*;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;

/** Two original NEI catalyst relationships, pointing to the existing unique alloy/RTG categories and server tables. */
@JeiPlugin public final class RtgBlastJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:rtg_blast_furnace");}
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){registration.addRecipeCatalysts(AlloyJeiPlugin.TYPE,RtgBlastContent.OFF_ITEM.get());registration.addRecipeCatalysts(RtgJeiPlugin.TYPE,RtgBlastContent.OFF_ITEM.get());}
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration){registration.addRecipeClickArea(RtgBlastScreen.class,101,35,24,17,AlloyJeiPlugin.TYPE);}
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){registration.addRecipeTransferHandler(RtgBlastMenu.class,RtgBlastContent.MENU.get(),AlloyJeiPlugin.TYPE,0,2,9,36);}
}
