package com.hbm.client.turret.jeremy;
import com.hbm.machine.turret.jeremy.JeremyTurretContent;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import mezz.jei.api.ingredients.subtypes.*;
import com.hbm.machine.turret.jeremy.CannonAmmoItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
@JeiPlugin public final class JeremyTurretJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:jeremy_turrets");}
    @Override public void registerItemSubtypes(ISubtypeRegistration event){event.registerSubtypeInterpreter(JeremyTurretContent.AMMO.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return JeremyTurretContent.AMMO.get().ordinal(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return CannonAmmoItem.NAMES.get(JeremyTurretContent.AMMO.get().ordinal(stack));}});}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration event){event.addExtraIngredients(VanillaTypes.ITEM_STACK,JeremyTurretContent.creativeStacks("weapon"));}
}
