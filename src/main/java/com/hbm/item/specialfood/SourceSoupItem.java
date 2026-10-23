package com.hbm.item.specialfood;

import com.hbm.item.structureloot.StructureFoodItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Actual vanilla1.7 ItemSoup(6): the source food consume path, then an unconditional bowl. */
public final class SourceSoupItem extends StructureFoodItem {
    public SourceSoupItem(String name) { super(name, 6, .6F, 1); }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack eaten = super.finishUsingItem(stack, level, consumer);
        return consumer instanceof Player ? new ItemStack(Items.BOWL) : eaten;
    }
}
