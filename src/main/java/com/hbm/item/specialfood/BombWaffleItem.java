package com.hbm.item.specialfood;

import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.item.structureloot.StructureFoodItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** X5846 ItemWaffle.onFoodEaten; delegates every blast/effect/radiation parameter to the existing Muke backend. */
public final class BombWaffleItem extends StructureFoodItem {
    public BombWaffleItem() { super("bomb_waffle", 20, .6F); }

    @Override
    protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide)
            ExplosionNukeSmall.explode(level, player.getX(), player.getY() + .5D, player.getZ(), ExplosionNukeSmall.PARAMS_MEDIUM);
    }
}
