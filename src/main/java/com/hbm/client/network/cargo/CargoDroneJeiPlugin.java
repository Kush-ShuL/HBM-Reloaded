package com.hbm.client.network.cargo;
import com.hbm.network.cargo.CargoDroneContent;
import com.hbm.network.cargo.DroneItem;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Ordinary physical drone items; no item-subtype interpreter is needed. */
@JeiPlugin public final class CargoDroneJeiPlugin implements IModPlugin {
 public static List<ItemStack> variants(){return Arrays.stream(DroneItem.Kind.values()).map(CargoDroneContent::droneStack).toList();}
 @Override public ResourceLocation getPluginUid(){return ResourceLocation.parse("hbm:cargo_drone_jei");}
 @Override public void registerExtraIngredients(IExtraIngredientRegistration registration){registration.addExtraIngredients(VanillaTypes.ITEM_STACK,variants());}
}
