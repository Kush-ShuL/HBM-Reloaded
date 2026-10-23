package com.hbm.client.network.cargo;
import com.hbm.network.cargo.CargoDroneContent;
import java.util.List;
import mezz.jei.api.*;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.*;
import mezz.jei.api.registration.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Real drone ordinal matching, four closed patrol variants; fifth becomes visible only with the actual request entity family. */
@JeiPlugin public final class CargoDroneJeiPlugin implements IModPlugin {
 public static List<ItemStack> variants(){int size=BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse("hbm:entity_request_drone"))?5:4;return java.util.stream.IntStream.range(0,size).mapToObj(CargoDroneContent.DRONE.get()::stack).toList();}
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:cargo_drone_jei");}@Override public void registerItemSubtypes(ISubtypeRegistration registration){registration.registerSubtypeInterpreter(CargoDroneContent.DRONE.get(),new ISubtypeInterpreter<ItemStack>(){@Override public Object getSubtypeData(ItemStack stack,UidContext context){return CargoDroneContent.DRONE.get().ordinal(stack);}@Override public String getLegacyStringSubtypeInfo(ItemStack stack,UidContext context){return getSubtypeData(stack,context).toString();}});}@Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,variants());}
}
