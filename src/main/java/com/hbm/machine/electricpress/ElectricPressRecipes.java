package com.hbm.machine.electricpress;

import com.hbm.machine.press.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Access the one existing PressRecipe manager, including its real configured-material WIRE holders. */
public final class ElectricPressRecipes {
    private ElectricPressRecipes(){}
    public static ItemStack output(Level level,ItemStack material,ItemStack stamp){if(material.isEmpty()||stamp.isEmpty())return ItemStack.EMPTY;return level.getRecipeManager().getRecipeFor(PressContent.RECIPE_TYPE.get(),new PressInput(material,stamp),level).map(holder->holder.value().result()).orElse(ItemStack.EMPTY);}
    public static boolean sameLegacy(ItemStack left,ItemStack right){return left.is(right.getItem())&&com.hbm.material.HbmMaterials.metadata(left)==com.hbm.material.HbmMaterials.metadata(right);}
    public static void wearStamp(ItemStack stack){if(stack.getMaxDamage()!=0){stack.setDamageValue(stack.getDamageValue()+1);if(stack.getDamageValue()>=stack.getMaxDamage())stack.setCount(0);}}
}
