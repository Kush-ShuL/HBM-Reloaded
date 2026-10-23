package com.hbm.weapon.sedna.gun;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import java.util.function.Consumer;

/** Original gun NBT compound, carried unchanged inside modern custom_data rather than synthetic item damage. */
public final class GunStackData {
    public static CompoundTag data(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(); }
    public static void update(ItemStack stack, Consumer<CompoundTag> mutation) { var data = data(stack); mutation.accept(data); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data)); }
    public static boolean hasData(ItemStack stack) { return stack.has(DataComponents.CUSTOM_DATA); }
    private GunStackData() {}
}
