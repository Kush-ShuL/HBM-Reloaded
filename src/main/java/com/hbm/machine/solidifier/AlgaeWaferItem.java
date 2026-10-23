package com.hbm.machine.solidifier;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/** Actual bio_wafer ItemLemon(4,2F,false): ordinary32tick food, no special ItemLemon identity branch. */
public final class AlgaeWaferItem extends Item {
    public AlgaeWaferItem(){super(new Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(2F).build()));}
}
