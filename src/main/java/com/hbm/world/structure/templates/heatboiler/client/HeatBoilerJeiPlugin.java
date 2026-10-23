package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerContent;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Reuse the existing actual boiling category; only supply source IBlockMulti raw metadata identity. */
@JeiPlugin
public final class HeatBoilerJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:heat_boiler_subtypes");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(HeatBoilerContent.ITEM.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return HeatBoilerContent.metadata(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return Integer.toString(HeatBoilerContent.metadata(stack));}});}
}
